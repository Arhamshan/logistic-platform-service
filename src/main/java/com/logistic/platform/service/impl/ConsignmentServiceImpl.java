package com.logistic.platform.service.impl;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Contact;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.Location;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.GetConsignmentResponseDto;
import com.logistic.platform.repository.reader.ConsignmentReaderRepository;
import com.logistic.platform.repository.writer.ConsignmentWriterRepository;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.ContactService;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.service.LocationService;
import com.logistic.platform.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConsignmentServiceImpl implements ConsignmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentServiceImpl.class);

    private final ContactService contactService;

    private final ItemService itemService;

    private final ConsignmentWriterRepository writerRepository;

    private final ConsignmentReaderRepository readerRepository;

    private final LocationService locationService;

    public ConsignmentServiceImpl(ContactService contactService,
                                  ItemService itemService,
                                  ConsignmentWriterRepository writerRepository,
                                  LocationService locationService,
                                  ConsignmentReaderRepository readerRepository) {

        this.contactService = contactService;
        this.itemService = itemService;
        this.writerRepository = writerRepository;
        this.locationService = locationService;
        this.readerRepository = readerRepository;
    }

    @Override
    @Transactional
    public List<ItemProcessResultVo> save(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] save: consignment={}",
                requestId, CommonUtils.convertToString(consignment));

        List<ItemProcessResultVo> results = new ArrayList<>();

        try {
            String locationCode = null;

            if (!consignment.getItems().isEmpty()) {
                locationCode = consignment.getItems().get(0).getCurrentLocationCode();
            }

            Location location = locationService.getLocationByCode(locationCode, requestId);

            if (location == null) {
                results.add(new ItemProcessResultVo(
                        null,
                        consignment.getConsignmentId(),
                        400,
                        "Location not found for the locationCode"
                ));

            } else {

                // Validate no duplicate itemIds within the consignment
                List<String> itemIds = consignment.getItems().stream()
                        .map(Item::getItemId)
                        .collect(Collectors.toList());

                Set<String> uniqueItemIds = new HashSet<>(itemIds);

                if (uniqueItemIds.size() != itemIds.size()) {
                    results.add(new ItemProcessResultVo(
                            null,
                            consignment.getConsignmentId(),
                            400,
                            "Duplicate itemId found within the consignment"
                    ));
                }

                Contact savedSenderContact = contactService.createContact(consignment.getSenderContact(), requestId);
                if (savedSenderContact != null) {
                    consignment.getSenderContact().setId(savedSenderContact.getId());
                }

                Contact savedDestinationContact = contactService.createContact(consignment.getDestinationContact(), requestId);
                if (savedDestinationContact != null) {
                    consignment.getDestinationContact().setId(savedDestinationContact.getId());
                }

                consignment.setStatus(ConsignmentStatus.BOOKED);

                Long consId = writerRepository.save(consignment, requestId);
                consignment.setId(consId);

                // One DB read — get last barcode
                String lastBarcode = itemService.getLastBarcodeNumber(requestId);

                // Generate all barcodes in memory
                List<String> barcodes = new ArrayList<>();
                for (int i = 0; i < consignment.getItems().size(); i++) {
                    String base = (i == 0) ? lastBarcode : barcodes.get(i - 1);
                    String newBarcode = itemService.generateBarcodeNumber(base, requestId);
                    barcodes.add(newBarcode);
                }

                // Save items with pre-generated barcodes
                for (int i = 0; i < consignment.getItems().size(); i++) {
                    Item item = consignment.getItems().get(i);
                    item.setConsignment(new Consignment(consignment.getId(), consignment.getConsignmentId()));
                    item.setStatus(ItemStatus.BOOKED);
                    item.setBarcodeNumber(barcodes.get(i));

                    try {
                        Boolean isItemSaved = itemService.save(item, requestId);

                        if (isItemSaved) {
                            results.add(new ItemProcessResultVo(
                                    item.getItemId(),
                                    consignment.getConsignmentId(),
                                    201,
                                    "Item created successfully"
                            ));
                        }
                    } catch (Exception e) {
                        results.add(new ItemProcessResultVo(
                                item.getItemId(),
                                consignment.getConsignmentId(),
                                400,
                                "Failed to create item"
                        ));
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] save: results={}|timeTaken={}",
                requestId, results, CommonUtils.getExecutionTime(startTime));

        return results;
    }

    @Override
    @Transactional
    public void updateStatus(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateStatus: consignment={}",
                requestId, CommonUtils.convertToString(consignment));

        try {

            writerRepository.updateStatus(consignment, requestId);

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateStatus: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }
    }

    // Validation for unique consignment #96
    @Override
    public Boolean existsByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] existsByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        boolean exists = false;

        try {
            exists = readerRepository.findByConsignmentId(consignmentId, requestId).isPresent();

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] existsByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] existsByConsignmentId: exists={}|timeTaken={}",
                    requestId, exists, CommonUtils.getExecutionTime(startTime));
        }

        return exists;
    }

    @Override
    public TrackingConsignmentVo getByConsignmentId(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getTrackingByConsignmentId: consignmentId={}",
                requestId, consignmentId);

        TrackingConsignmentVo result = null;

        try {
            Optional<Consignment> consignmentOpt = readerRepository.findTrackingByConsignmentId(consignmentId, requestId);

            if (consignmentOpt.isEmpty()) {
                return null;
            }

            Consignment consignment = consignmentOpt.get();

            result = new TrackingConsignmentVo();
            result.setConsignmentId(consignment.getConsignmentId());
            result.setStatus(consignment.getStatus().name());

            // item Tracking part
            List<TrackingItemVo> trackingItems = itemService.getTrackingItems(consignmentId, requestId);

            // Resolve current_location for each item
            for (TrackingItemVo itemVo : trackingItems) {
                Location location = locationService.getLocationByCode(itemVo.getCurrentLocationCode(), requestId);
                if (location != null) {
                    itemVo.setCurrentLocation(
                            new TrackingLocationVo(
                                    location.getName(),
                                    location.getType().name(),
                                    location.getLocationCode()
                            )
                    );
                }
            }

            result.setItems(trackingItems);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getTrackingByConsignmentId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getTrackingByConsignmentId: result={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(result), CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    @Override
    public SummaryVo getSummary(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getSummary", requestId);

        SummaryVo result = null;

        try {
            result = readerRepository.findSummary(requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getSummary: result={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(result), CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    @Override
    public List<Consignment> getAllConsignments(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getAllConsignments : pageNumber={}|pageSize={}|sortBy={}|sortDir={}",
                requestId, pageNumber, pageSize, sortBy, sortDir);

        List<Consignment> consignments = null;

        try {
            consignments = readerRepository.findAll(pageNumber, pageSize, sortBy, sortDir, requestId);

            if (consignments != null) {
                consignments.forEach(con -> {
                    con.setSenderContact(contactService.getById(con.getSenderContact().getId(), requestId));
                    con.setDestinationContact(contactService.getById(con.getDestinationContact().getId(), requestId));

                    List<Item> items = itemService.getByConsId(con.getId(), requestId);

                    con.setItems(items);

                    if (items != null && !items.isEmpty()) {
                        con.setCurrentLocationCode(items.get(0).getCurrentLocationCode());
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getAllConsignments: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getAllConsignments: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }

        return consignments;
    }

    @Override
    public Boolean deleteById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] deleteById: id={}",
                requestId, id);

        Boolean isDeleted = Boolean.FALSE;

        try {
            isDeleted = writerRepository.deleteById(id, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] deleteById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] deleteById: isDeleted={}|timeTaken={}",
                    requestId, isDeleted, CommonUtils.getExecutionTime(startTime));
        }

        return isDeleted;
    }

    @Override
    public ConsignmentVo getById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getById: id={}", requestId, id);

        ConsignmentVo result = null;

        try {
            result = readerRepository.findById(id, requestId).orElse(null);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getById: result={}|timeTaken={}",
                    requestId, CommonUtils.convertToString(result), CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}
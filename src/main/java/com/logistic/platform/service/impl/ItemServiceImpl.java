package com.logistic.platform.service.impl;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.EventType;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.ItemReaderRepository;
import com.logistic.platform.repository.writer.ItemWriterRepository;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.util.ConsignmentUtil;
import com.logistic.platform.vo.TrackingItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemWriterRepository writerRepository;

    private final ItemReaderRepository itemReaderRepository;

    private final EventService eventService;

    private final ConsignmentService consignmentService;


    public ItemServiceImpl(ItemWriterRepository writerRepository, ItemReaderRepository itemReaderRepository,
                           EventService eventService, @Lazy ConsignmentService consignmentService) {
        this.writerRepository = writerRepository;
        this.itemReaderRepository = itemReaderRepository;
        this.eventService = eventService;
        this.consignmentService = consignmentService;
    }

    @Override
    @Transactional
    public Boolean save(Item item, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] save: item={}",
                requestId, CommonUtils.convertToString(item));

        Boolean isItemSaved = Boolean.FALSE;

        try {
            Long itemConsId = writerRepository.save(item, requestId);

            if (itemConsId != null) {
                isItemSaved = Boolean.TRUE;

                item.setId(itemConsId);
            }
            Event eventByItem = getEventByItem(item,item.getStatus());

            eventService.saveEvent(eventByItem, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] save: isItemSaved={}|timeTaken={}",
                requestId, isItemSaved, CommonUtils.getExecutionTime(startTime));

        return isItemSaved;
    }

    private Event getEventByItem(Item item, ItemStatus itemStatus) {
        Event event = new Event();

        event.setItem(item);
        event.setEventLocationCode(item.getCurrentLocationCode());
        event.setEventType(ConsignmentUtil.getEventTypeByItemStatus(itemStatus));
        event.setDescription(itemStatus.getDescription());
        event.setCreatedBy(item.getCreatedBy());

        return event;
    }


    @Override
    public Item getItemByConsignmentIdAndItemId(String consignmentId, String itemId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getItemByConsignmentIdAndItemId: consignmentId={}|itemId={}",
                requestId, consignmentId, itemId);

        Item item = null;

        try {

            item = itemReaderRepository.findByConsignmentIdAndItemId(consignmentId, itemId, requestId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Item not found: " + itemId));

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getItemByConsignmentIdAndItemId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getItemByConsignmentIdAndItemId: itemId={}|timeTaken={}",
                    requestId, itemId, CommonUtils.getExecutionTime(startTime));
        }

        return item;
    }

    @Override
    @Transactional
    public void updateStatusAndLocation(Item item, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateStatusAndLocation: item={}",
                requestId, CommonUtils.convertToString(item));

        try {

            writerRepository.updateItemStatusAndLocation(item, requestId);

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateStatusAndLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateStatusAndLocation: itemId={}|timeTaken={}",
                    requestId, item.getItemId(), CommonUtils.getExecutionTime(startTime));
        }
    }

    // Item tracing for consignment #97
    @Override
    public List<TrackingItemVo> getTrackingItems(String consignmentId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getTrackingItems: consignmentId={}",
                requestId, consignmentId);

        List<TrackingItemVo> result = new ArrayList<>();

        try {
            List<Item> items = itemReaderRepository.findItemsByConsignmentId(consignmentId, requestId);

            if (items != null) {
                for (Item item : items) {
                    TrackingItemVo itemVo = new TrackingItemVo();
                    itemVo.setId(item.getId());
                    itemVo.setItemId(item.getItemId());
                    itemVo.setStatus(item.getStatus().name());
                    itemVo.setBarcode(item.getBarcodeNumber());
                    itemVo.setWeight(Double.valueOf(item.getWeight()));
                    itemVo.setHeight(Double.valueOf(item.getHeight()));
                    itemVo.setWidth(Double.valueOf(item.getWidth()));
                    itemVo.setLength(Double.valueOf(item.getLength()));
                    itemVo.setCurrentLocationCode(item.getCurrentLocationCode());
                    itemVo.setTracking(eventService.getTrackingEvents(item.getId(), requestId));
                    result.add(itemVo);
                }
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getTrackingItems: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getTrackingItems: count={}|timeTaken={}",
                    requestId, result.size(), CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    // Scan Items by Barcode Number #101
    @Override
    public String generateBarcodeNumber(String lastBarcode, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] generateBarcodeNumber", requestId);

        String newBarcodeNumber = null;

        try {
            if (CommonUtils.isBlankString(lastBarcode)) {
                newBarcodeNumber = "BRC_0000000001";

            } else {
                String prefix = lastBarcode.substring(0, 4);
                int number = Integer.parseInt(lastBarcode.substring(4));
                number++;
                newBarcodeNumber = prefix + String.format("%010d", number);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] generateBarcodeNumber: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] generateBarcodeNumber: newBarcodeNumber={}|timeTaken={}",
                    requestId, newBarcodeNumber, CommonUtils.getExecutionTime(startTime));
        }

        return newBarcodeNumber;
    }

    @Override
    public String getLastBarcodeNumber(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getLastBarcodeNumber", requestId);

        String lastBarcode = null;

        try {
            lastBarcode = itemReaderRepository.findLastBarcodeNumber(requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getLastBarcodeNumber: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getLastBarcodeNumber: lastBarcode={}|timeTaken={}",
                    requestId, lastBarcode, CommonUtils.getExecutionTime(startTime));
        }

        return lastBarcode;
    }

    @Override
    public Item getItemByBarcodeNumber(String barcodeNumber, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getItemByBarcodeNumber: barcodeNumber={}",
                requestId, barcodeNumber);

        Item item = null;

        try {
            item = itemReaderRepository.findByBarcodeNumber(barcodeNumber, requestId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Item not found for barcode: " + barcodeNumber));

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getItemByBarcodeNumber: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getItemByBarcodeNumber: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }

        return item;
    }

    @Override
    @Transactional
    public Boolean updateStatus(Long id, String status, String locationCode, String requestId, String username) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateStatus: id={}|status={}|locationCode={}",
                requestId, id, status, locationCode);

        Boolean isItemUpdated = Boolean.FALSE;
        Item item = null;

        try {
            // 1. Fetch item by PK
            item = itemReaderRepository.findById(id, requestId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found for id: " + id));

            // 2. Map EventType → ItemStatus
            EventType eventType = ConsignmentUtil.getEventTypeByItemStatus(ItemStatus.valueOf(status));

            item.setStatus(ConsignmentUtil.mapItemStatus(eventType));
            item.setCurrentLocationCode(locationCode);
            item.setUpdatedDate(LocalDateTime.now());
            item.setUpdatedBy(username);

            // 3. Update item
            isItemUpdated = writerRepository.updateItemStatusAndLocation(item, requestId);

            if (isItemUpdated) {
                // 4. Derive consignment status from ALL items (supports partial statuses)
                Consignment consignment = item.getConsignment();

                if (consignment != null) {
                    // Fetch every item that belongs to this consignment
                    List<Item> allItems = getByConsId(consignment.getId(), requestId);

                    // Collect their current statuses (override triggering item's status in case reader DB is stale)
                    Item finalItem = item;
                    List<ItemStatus> allStatuses = allItems.stream()
                            .map(it -> java.util.Objects.equals(it.getId(), finalItem.getId())
                                    ? finalItem.getStatus()
                                    : it.getStatus())
                            .collect(Collectors.toList());

                    // Derive the correct consignment status (full or partial)
                    ConsignmentStatus derivedStatus =
                            ConsignmentUtil.deriveConsignmentStatusFromItems(allStatuses);

                    consignment.setStatus(derivedStatus);
                    consignment.setUpdatedDate(LocalDateTime.now());
                    consignment.setUpdatedBy(username);

                    consignmentService.updateStatus(consignment, requestId);
                }

                // 5. Save event
                Event event = new Event();
                event.setItem(item);
                event.setEventType(eventType);
                event.setEventLocationCode(locationCode);
                event.setDescription(eventType.name());
                event.setCreatedBy(username);
                event.setUpdatedBy(username);

                eventService.saveEvent(event, requestId);

            }


        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateStatus: isUpdated={}|timeTaken={}",
                    requestId, isItemUpdated, CommonUtils.getExecutionTime(startTime));
        }

        return isItemUpdated;
    }

    @Override
    public List<Item> getByConsId(Long consId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getByConsId: consId={}", requestId, consId);

        List<Item> items = itemReaderRepository.findByConsId(consId, requestId);

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getByConsId: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return items;
    }

    @Override
    @Transactional
    public Boolean updateItems(List<Item> items, String username, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateItems: itemCount={}",
                requestId, items.size());

        Boolean isUpdated = Boolean.FALSE;

        try {
            isUpdated = writerRepository.updateItems(items, username, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateItems: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateItems: isUpdated={}|timeTaken={}",
                    requestId, isUpdated, CommonUtils.getExecutionTime(startTime));
        }

        return isUpdated;
    }


    @Override
    public List<Item> getItemsByStatus(String status, Boolean isSkipDriverAssignment, int pageNumber, int pageSize,
                                       String sortBy, String sortDir, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getItemsByStatus: status={}|isSkipDriverAssignment={}|pageNumber={}|pageSize={}|sortBy={}|sortDir={}",
                requestId, status, isSkipDriverAssignment, pageNumber, pageSize, sortBy, sortDir);

        List<Item> result = null;

        try {
            try {
                ItemStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid item status: " + status);
            }

            result = itemReaderRepository.findAllByStatus(status.toUpperCase(), isSkipDriverAssignment, pageNumber, pageSize, sortBy, sortDir, requestId);

            if (result == null || result.isEmpty()) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] getItemsByStatus: No items found for status={}",
                        requestId, status);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getItemsByStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getItemsByStatus: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    @Override
    public Integer getCountOfItemsByStatus(String status, Boolean isSkipDriverAssignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getCountOfItemsByStatus: status={}|isSkipDriverAssignment={}", requestId, status, isSkipDriverAssignment);

        Integer allCount = itemReaderRepository.findCountOfItemsByStatus(status, isSkipDriverAssignment, requestId);


        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getCountOfItemsByStatus: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return allCount;
    }

    @Override
    public List<Item> getItemsByDriverId(Long driverId, String status, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getItemsByDriverId: driverId={}|status={}",
                requestId, driverId, status);

        List<Item> result = null;

        try {
            if (driverId == null || driverId <= 0) {
                throw new IllegalArgumentException("Invalid driverId: " + driverId);
            }

            // Validate status only if provided
            if (status != null && !status.isBlank()) {
                try {
                    ItemStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid item status: " + status);
                }
            }

            result = itemReaderRepository.findAllByDriverId(driverId, status, requestId);

            if (result == null || result.isEmpty()) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] getItemsByDriverId: No items found for driverId={}|status={}",
                        requestId, driverId, status);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getItemsByDriverId: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getItemsByDriverId: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}
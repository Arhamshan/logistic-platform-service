package com.logistic.platform.service.impl;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Contact;
import com.logistic.common.entity.Location;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.ConsignmentWriterRepository;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.ContactService;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.vo.ItemProcessResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsignmentServiceImpl implements ConsignmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsignmentServiceImpl.class);

    private final ContactService contactService;

    private final EventService eventService;

    private final ItemService itemService;

    private final ConsignmentWriterRepository writerRepository;

    private final LocationServiceImpl locationService;

    public ConsignmentServiceImpl(ContactService contactService,
                                  EventService eventService,
                                  ItemService itemService,
                                  ConsignmentWriterRepository writerRepository,
                                  LocationServiceImpl locationService) {

        this.contactService = contactService;
        this.eventService = eventService;
        this.itemService = itemService;
        this.writerRepository = writerRepository;
        this.locationService = locationService;
    }

    @Override
    @Transactional
    public List<ItemProcessResultVo> save(Consignment consignment, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] save: consignment={}",
                requestId, CommonUtils.convertToString(consignment));

        List<ItemProcessResultVo> results = new ArrayList<>(  );

        try {
            //  location code validation
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
                // save sender contact
                Contact savedSenderContact = contactService.createContact(consignment.getSenderContact(), requestId);

                if (savedSenderContact != null) {
                    consignment.getSenderContact().setId(savedSenderContact.getId());
                }

                // save destination contact
                Contact savedDestinationContact = contactService.createContact(consignment.getDestinationContact(), requestId);

                if (savedDestinationContact != null) {
                    consignment.getDestinationContact().setId(savedDestinationContact.getId());
                }

                consignment.setStatus(ConsignmentStatus.BOOKED);

                // save consignment
                Long consId = writerRepository.save(consignment, requestId);
                consignment.setId(consId);

                // save items and generate response
                consignment.getItems().forEach(item -> {
                    item.setConsignment(new Consignment(consignment.getId(), consignment.getConsignmentId()));
                    item.setStatus(ItemStatus.BOOKED);

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
                });
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
}
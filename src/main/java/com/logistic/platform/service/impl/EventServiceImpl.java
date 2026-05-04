package com.logistic.platform.service.impl;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.ItemStatus;
import com.logistic.platform.repository.writer.ConsignmentWriterRepository;
import com.logistic.platform.repository.writer.EventWriterRepository;
import com.logistic.platform.repository.writer.ItemWriterRepository;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.EventService;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventWriterRepository eventWriterRepository;
    //private final ItemWriterRepository itemWriterRepository;
    //private final ConsignmentWriterRepository consignmentWriterRepository;

    private final ItemService itemService;
    private final ConsignmentService consignmentService;

    public EventServiceImpl(EventWriterRepository eventWriterRepository,
                            ItemService itemService, ConsignmentService consignmentService) {
        this.eventWriterRepository = eventWriterRepository;
        this.itemService = itemService;
        this.consignmentService = consignmentService;
    }

    @Override
    @Transactional
    public Event saveEvent(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] saveEvent: event={}", requestId, CommonUtils.convertToString(event));

        Event savedEvent = null;

        try {
            if (event.getCreatedDate() == null) {
                event.setCreatedDate(LocalDateTime.now());
            }
            if (event.getUpdatedDate() == null) {
                event.setUpdatedDate(LocalDateTime.now());
            }

            savedEvent = eventWriterRepository.save(event, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] saveEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] saveEvent: eventId={} | timeTaken={}",
                requestId, savedEvent.getId(), CommonUtils.getExecutionTime(startTime));

        return savedEvent;
    }

    @Override
    @Transactional
    public void createEvent(Event event, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createEvent: event={}",
                requestId, CommonUtils.convertToString(event));

        try {

            // 1. Validate request
            if (event.getItem() == null) {
                throw new IllegalArgumentException("Item details are required");
            }

            if (event.getItem().getItemId() == null) {
                throw new IllegalArgumentException("ItemId is required");
            }

            if (event.getItem().getConsignment() == null ||
                    event.getItem().getConsignment().getConsignmentId() == null) {

                throw new IllegalArgumentException("ConsignmentId is required");
            }

            if (event.getEventLocationCode() == null) {
                throw new IllegalArgumentException("LocationCode is required");
            }

            if (event.getEventType() == null) {
                throw new IllegalArgumentException("EventType is required");
            }

            String itemId = event.getItem().getItemId();

            String consignmentId = event.getItem()
                    .getConsignment()
                    .getConsignmentId();

            // 2. Fetch & validate item + consignment relationship
            Item item = itemService.getItemByConsignmentIdAndItemId(
                    itemId,
                    consignmentId,
                    requestId
            );

            if (item == null) {
                throw new IllegalArgumentException(
                        "Item not found for itemId=" + itemId +
                                " and consignmentId=" + consignmentId
                );
            }

            // 3. Bind DB item to event
            event.setItem(item);

            // 4. Set audit fields
            event.setCreatedDate(LocalDateTime.now());
            event.setUpdatedDate(LocalDateTime.now());
            event.setCreatedBy("SYSTEM");
            event.setUpdatedBy("SYSTEM");

            // 5. Save Event
            saveEvent(event, requestId);

            // 6. Update Item
            updateItem(item, event, requestId);

            // 7. Update Consignment
            Consignment consignment = consignmentService
                    .getByConsignmentId(item.getConsignment().getConsignmentId(), requestId);

            updateConsignment(consignment, event, requestId);

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createEvent SUCCESS | timeTaken={}",
                    requestId,
                    CommonUtils.getExecutionTime(startTime));

        } catch (IllegalArgumentException | IllegalStateException e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createEvent: {}",
                    requestId,
                    e.getMessage());

            throw e;

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createEvent failed",
                    requestId,
                    e);

            throw new RuntimeException("Failed to create event", e);
        }
    }

    private void updateItem(Item item, Event event, String requestId) {
        item.setStatus(mapItemStatus(event.getEventType().name()));
        item.setCurrentLocationCode(event.getEventLocationCode());
        item.setUpdatedDate(LocalDateTime.now());

        // need item service call - done
        itemService.updateStatusAndLocation(item, requestId);
    }

    private void updateConsignment(Consignment consignment, Event event, String requestId) {
        consignment.setStatus(mapConsignmentStatus(event.getEventType().name()));
        consignment.setUpdatedDate(LocalDateTime.now());

        // need consignment service call - done
        consignmentService.updateConsignmentStatus(consignment, requestId);
    }

    private ItemStatus mapItemStatus(String eventType) {
        switch (eventType) {
            case "PARCEL_PICKED_UP": return ItemStatus.PICKED_UP;
            case "IN_TRANSIT": return ItemStatus.IN_TRANSIT;
            case "OUT_FOR_DELIVERY": return ItemStatus.OUT_FOR_DELIVERY;
            case "DELIVERED": return ItemStatus.DELIVERED;
            default: return ItemStatus.BOOKED;
        }
    }

    private ConsignmentStatus mapConsignmentStatus(String eventType) {
        switch (eventType) {
            case "PARCEL_PICKED_UP": return ConsignmentStatus.PICKED_UP;
            case "IN_TRANSIT": return ConsignmentStatus.IN_TRANSIT;
            case "OUT_FOR_DELIVERY": return ConsignmentStatus.OUT_FOR_DELIVERY;
            case "DELIVERED": return ConsignmentStatus.DELIVERED;
            default: return ConsignmentStatus.BOOKED;
        }
    }
    }
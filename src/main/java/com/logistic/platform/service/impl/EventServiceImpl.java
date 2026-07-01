package com.logistic.platform.service.impl;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.EventType;
import com.logistic.common.enums.ItemStatus;
import com.logistic.platform.repository.reader.EventReaderRepository;
import com.logistic.platform.repository.writer.EventWriterRepository;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.EventService;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.util.ConsignmentUtil;
import com.logistic.platform.vo.TrackingEventVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventWriterRepository eventWriterRepository;
    private final ItemService itemService;
    private final ConsignmentService consignmentService;
    private final EventReaderRepository eventReaderRepository;

    public EventServiceImpl(EventWriterRepository eventWriterRepository,
                            @Lazy ItemService itemService, @Lazy ConsignmentService consignmentService,
                            EventReaderRepository eventReaderRepository) {  // @Lazy breaks the cycle
        this.eventWriterRepository = eventWriterRepository;
        this.itemService = itemService;
        this.consignmentService = consignmentService;
        this.eventReaderRepository = eventReaderRepository;
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
            // 1. Extract itemId & consignmentId from event
            String itemId = event.getItem().getItemId();

            String consignmentId = event.getItem()
                    .getConsignment()
                    .getConsignmentId();

            // 2. Fetch & validate item + consignment relationship
            Item item = itemService.getItemByConsignmentIdAndItemId(
                    event.getItem().getConsignment().getConsignmentId(),
                    event.getItem().getItemId(),
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

            // 5. Save Event
            saveEvent(event, requestId);

            // 6. Update Item
            updateItem(item, event, requestId);

            // 7. Get consignment from already fetched item — no extra DB call needed
            Consignment consignment = item.getConsignment();

            // 8. Update Consignment
            updateConsignment(consignment, item, requestId);

        }catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createEvent: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createEvent: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }
    }

    private void updateItem(Item item, Event event, String requestId) {
        item.setStatus(ConsignmentUtil.mapItemStatus(EventType.valueOf(event.getEventType().name())));
        item.setCurrentLocationCode(event.getEventLocationCode());
        item.setUpdatedDate(LocalDateTime.now());

        itemService.updateStatusAndLocation(item, requestId);
    }

    private void updateConsignment(Consignment consignment, Item updatedItem, String requestId) {

        // Fetch ALL items of this consignment to derive correct status
        List<Item> allItems = itemService.getByConsId(consignment.getId(), requestId);

        // Collect current statuses — updateItem() already persisted the triggering item's new status
        List<ItemStatus> allStatuses = allItems.stream()
                .map(it -> it.getId().equals(updatedItem.getId())
                        ? updatedItem.getStatus()
                        : it.getStatus())
                .collect(Collectors.toList());

        // Derive — this is in ConsignmentUtil which is already imported in this file
        ConsignmentStatus derivedStatus =
                ConsignmentUtil.deriveConsignmentStatusFromItems(allStatuses);

        consignment.setStatus(derivedStatus);
        consignment.setUpdatedDate(LocalDateTime.now());

        consignmentService.updateStatus(consignment, requestId);
    }
    // event tracking #97
    @Override
    public List<TrackingEventVo> getTrackingEvents(Long itemId, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getTrackingEvents: itemId={}",
                requestId, itemId);

        List<TrackingEventVo> result = null;

        try {
            result = eventReaderRepository.findTrackingEventsByItemId(itemId, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getTrackingEvents: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getTrackingEvents: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    }
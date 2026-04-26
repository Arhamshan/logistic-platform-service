package com.logistic.platform.service.impl;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.ItemWriterRepository;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.util.ConsignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemWriterRepository writerRepository;

    private final EventService eventService;

    public ItemServiceImpl(ItemWriterRepository writerRepository, EventService eventService) {
        this.writerRepository = writerRepository;
        this.eventService = eventService;
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

                Event eventByItem = getEventByItem(item, ItemStatus.BOOKED);

                eventService.saveEvent(eventByItem, requestId);
            }

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

}
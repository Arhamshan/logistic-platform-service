package com.logistic.platform.service.impl;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.ItemReaderRepository;
import com.logistic.platform.repository.writer.ItemWriterRepository;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.util.ConsignmentUtil;
import com.logistic.platform.vo.TrackingItemVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemWriterRepository writerRepository;

    private final ItemReaderRepository itemReaderRepository;

    private final EventService eventService;


    public ItemServiceImpl(ItemWriterRepository writerRepository, ItemReaderRepository itemReaderRepository, EventService eventService) {
        this.writerRepository = writerRepository;
        this.itemReaderRepository = itemReaderRepository;
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
                    itemVo.setItemId(item.getItemId());
                    itemVo.setStatus(item.getStatus().name());
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
}
package com.logistic.platform.service.impl;

import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.ItemReaderRepository;
import com.logistic.platform.repository.writer.ItemWriterRepository;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.util.ConsignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemWriterRepository writerRepository;

    private final ItemReaderRepository itemReaderRepository;


    public ItemServiceImpl(ItemWriterRepository writerRepository, ItemReaderRepository itemReaderRepository) {
        this.writerRepository = writerRepository;
        this.itemReaderRepository = itemReaderRepository;
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

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] save: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;
        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] save: isItemSaved={}|timeTaken={}",
                requestId, isItemSaved, CommonUtils.getExecutionTime(startTime));

        return isItemSaved;
    }

    @Override
    public Item getItemByConsignmentIdAndItemId(String itemId,
                            String consignmentId,
                            String requestId) {

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getItemById: itemId={} | consignmentId={}",
                requestId, itemId, consignmentId);

        try {

            Item item = itemReaderRepository.findByItemId(itemId, consignmentId, requestId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Item not found: " + itemId));

            // Validate item-consignment relationship
            if (item.getConsignment() == null ||
                    item.getConsignment().getConsignmentId() == null) {

                throw new IllegalStateException(
                        "Consignment not assigned for itemId=" + itemId);
            }

            String dbConsignmentId =
                    item.getConsignment().getConsignmentId();

            if (!dbConsignmentId.equals(consignmentId)) {

                throw new IllegalArgumentException(
                        "Item does not belong to consignmentId=" + consignmentId);
            }

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getItemById SUCCESS",
                    requestId);

            return item;

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getItemById failed",
                    requestId, e);

            throw e;
        }
    }

    @Override
    @Transactional
    public void updateStatusAndLocation(Item item, String requestId) {

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateStatusAndLocation",
                requestId);

        try {

            writerRepository.updateItemStatusAndLocation(item, requestId);

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateStatusAndLocation failed",
                    requestId, e);

            throw e;
        }
    }

}
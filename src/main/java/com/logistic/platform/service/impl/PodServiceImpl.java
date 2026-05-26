package com.logistic.platform.service.impl;

import com.logistic.common.entity.Item;
import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.PodWriterRepository;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.service.PodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PodServiceImpl implements PodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PodServiceImpl.class);

    private final PodWriterRepository podWriterRepository;
    private final ItemService itemService;

    public PodServiceImpl(PodWriterRepository podWriterRepository,
                          ItemService itemService) {
        this.podWriterRepository = podWriterRepository;
        this.itemService = itemService;
    }

    @Override
    public Boolean savePod(String consignmentId, String itemId,
                           Pod pod, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] savePod: consignmentId={}|itemId={}|pod={}",
                requestId, consignmentId, itemId, CommonUtils.convertToString(pod));

        Boolean result = Boolean.FALSE;

        try {
            // 1. Validate item belongs to consignment
            Item item = itemService.getItemByConsignmentIdAndItemId(
                    consignmentId, itemId, requestId);

            if (item == null) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] savePod: item not found for consignmentId={}|itemId={}",
                        requestId, consignmentId, itemId);
                return Boolean.FALSE;
            }

            // 3. Save
            Long savedId = podWriterRepository.save(pod, requestId);
            result = savedId != null;

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] savePod: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] savePod: result={}|timeTaken={}",
                    requestId, result, CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}
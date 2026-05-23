package com.logistic.platform.service.impl;

import com.logistic.common.entity.Item;
import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.pod.PodRequestDto;
import com.logistic.platform.repository.writer.PodWriterRepository;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.service.PodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
                           PodRequestDto requestDto, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] savePod: consignmentId={}|itemId={}",
                requestId, consignmentId, itemId);

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

            // 2. Build Pod entity
            Pod pod = new Pod();
            pod.setItem(item);
            pod.setReceivedBy(requestDto.getReceivedBy());
            pod.setReceiverContact(requestDto.getReceiverContact());
            pod.setRemarks(requestDto.getRemarks());
            pod.setPodPath(requestDto.getPodImage());   // base64 → pod_path
            pod.setDeliveredAt(requestDto.getReceivedAt() != null
                    ? OffsetDateTime.parse(requestDto.getReceivedAt()).toLocalDateTime()
                    : null);
            pod.setDeliveredBy(null);                   // not in request for now
            pod.setCreatedDate(LocalDateTime.now());
            pod.setCreatedBy("SYSTEM");
            pod.setUpdatedDate(LocalDateTime.now());
            pod.setUpdatedBy("SYSTEM");

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
package com.logistic.platform.service.impl;

import com.logistic.common.entity.Item;
import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.writer.PodWriterRepository;
import com.logistic.platform.service.ItemService;
import com.logistic.platform.service.PodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class PodServiceImpl implements PodService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PodServiceImpl.class);

    @Value("${logistic.pod.save.path}")
    private String podSavePath;

    @Value("${logistic.pod.get.url}")
    private String podGetUrl;

    private final PodWriterRepository podWriterRepository;
    private final ItemService itemService;

    public PodServiceImpl(PodWriterRepository podWriterRepository,
                          ItemService itemService) {
        this.podWriterRepository = podWriterRepository;
        this.itemService = itemService;
    }

    @Override
    public Boolean savePod(String consignmentId, String itemId,
                           Pod pod, String image, String requestId) {

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

            // save image in project file
            String filePath = saveImageFile(image);

            pod.setItem(item);
            pod.setPodPath(filePath);

            // 3. Save pod
            Long savedId = podWriterRepository.save(pod, requestId);
            result = savedId != null;

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] savePod: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] savePod: result={}|timeTaken={}",
                    requestId, result, CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }

    private String saveImageFile(String base64) throws IOException {

        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        String fileName = UUID.randomUUID() + ".jpg";

        Path uploadPath = Paths.get(podSavePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);

        Files.write(filePath, imageBytes);

        // return image path
        return "/" + podGetUrl + "/" + fileName;
    }
}
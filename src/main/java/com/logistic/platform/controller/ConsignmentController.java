package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Consignment;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.CreateConsignmentRequestDto;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.util.ConsignmentValidationUtil;
import com.logistic.platform.vo.ItemProcessResultVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/consignment")
public class ConsignmentController {

    private static final Logger LOGGER = LogManager.getLogger(ConsignmentController.class);

    private final ConsignmentService service;

    public ConsignmentController(ConsignmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<List<ItemProcessResultVo>>> createConsignment(
            @RequestBody CreateConsignmentRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createLocation: ", requestId);

        ResponseDto<List<ItemProcessResultVo>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Consignment consignment = requestDto.getConsignment();

            //  Validate consignment first
            String validationError = ConsignmentValidationUtil.validate(requestDto);
            if (validationError != null) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage(validationError);
                response.setData(null);
                return ResponseEntity.ok(response);
            }

            // Check for duplicate consignmentId #96
            boolean exists = service.existsByConsignmentId(
                    requestDto.getConsignmentId(), requestId);

            if (exists) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Duplicate consignmentId");
                response.setData(null);
                return ResponseEntity.ok(response);
            }

            List<ItemProcessResultVo> results = service.save(consignment, requestId);

            if (results != null && !results.isEmpty() && results.get(0).getItemId() == null && results.get(0).getStatusCode() == 400) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage(results.get(0).getMessage()); // Location not found for the locationCode
                response.setData(null);

            } else if (results == null || results.isEmpty()) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to create consignment.");

            } else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Consignment created successfully.");
                response.setData(results);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to create consignment.");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createLocation: Ex={}|Trace={}", requestId,
                    e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] createLocation: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

}

package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Consignment;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.CreateConsignmentRequestDto;
import com.logistic.platform.dto.consignment.SummaryResponseDto;
import com.logistic.platform.dto.consignment.TrackingConsignmentResponseDto;
import com.logistic.platform.dto.item.ItemProcessDto;
import com.logistic.platform.dto.pod.PodRequestDto;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.PodService;
import com.logistic.platform.util.ConsignmentValidationUtil;
import com.logistic.platform.vo.ItemProcessResultVo;
import com.logistic.platform.vo.SummaryVo;
import com.logistic.platform.vo.TrackingConsignmentVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/consignment")
public class ConsignmentController {

    private static final Logger LOGGER = LogManager.getLogger(ConsignmentController.class);

    private final ConsignmentService service;
    private final PodService podService;

    public ConsignmentController(ConsignmentService service, PodService podService) {
        this.service = service;
        this.podService = podService;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<List<ItemProcessDto>>> createConsignment(
            @RequestBody CreateConsignmentRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createLocation: ", requestId);

        ResponseDto<List<ItemProcessDto>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Consignment consignment = requestDto.getConsignment();

            //  Validate consignment first
            String validationError = ConsignmentValidationUtil.validate(requestDto);

            if (validationError != null) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage(validationError);
                response.setData(null);

            } else {
                // Check for duplicate consignmentId #96
                boolean exists = service.existsByConsignmentId(
                        requestDto.getConsignmentId(), requestId);

                if (exists) {
                    response.setResponseCode(HttpStatus.OK.value());
                    response.setResponseMessage("Duplicate consignmentId");
                    response.setData(null);

                } else {
                    List<ItemProcessResultVo> results = service.save(consignment, requestId);

                    if (results != null && !results.isEmpty() && results.get(0).getItemId() == null && results.get(0).getStatusCode() == 400) {
                        response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                        response.setResponseMessage(results.get(0).getMessage()); // Location not found for the locationCode
                        response.setData(null);

                    } else if (results == null || results.isEmpty()) {
                        response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                        response.setResponseMessage("Failed to create consignment.");

                    } else {
                        List<ItemProcessDto> resultDtoList = results.stream().map(vo -> new ItemProcessDto(vo))
                                .collect(Collectors.toList());

                        response.setResponseCode(HttpStatus.OK.value());
                        response.setResponseMessage("Consignment created successfully.");
                        response.setData(resultDtoList);
                    }
                }
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

    @GetMapping("/track/{consignmentId}")
    public ResponseEntity<ResponseDto<List<TrackingConsignmentResponseDto>>> trackConsignment(
            @PathVariable("consignmentId") String consignmentId,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] trackConsignment: consignmentId={}",
                requestId, consignmentId);

        ResponseDto<List<TrackingConsignmentResponseDto>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            TrackingConsignmentVo tracking = service.getByConsignmentId(consignmentId, requestId);

            if (tracking == null) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Tracking not found for the consignment " + consignmentId);
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Consignment tracking fetched successfully.");
                response.setData(List.of(new TrackingConsignmentResponseDto(tracking)));
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get tracking");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] trackConsignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] trackConsignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<ResponseDto<SummaryResponseDto>> getSummary(
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getSummary", requestId);

        ResponseDto<SummaryResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            SummaryVo summary = service.getSummary(requestId);

            if (summary == null) {

                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to get summary");
                response.setData(null);

            } else {
                SummaryResponseDto summaryResponse = new SummaryResponseDto(summary);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Summary fetched successfully");
                response.setData(summaryResponse);
            }

        } catch (Exception e) {

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get summary");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {

            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] getSummary: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    // POD #99
    @PostMapping("/{consignmentId}/pod")
    public ResponseEntity<ResponseDto<Void>> savePod(
            @PathVariable("consignmentId") String consignmentId,
            @RequestParam("itemId") String itemId,
            @RequestBody PodRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] savePod: consignmentId={}|itemId={}",
                requestId, consignmentId, itemId);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Boolean saved = podService.savePod(consignmentId, itemId, requestDto, requestId);

            if (Boolean.FALSE.equals(saved)) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to save POD");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("POD saved successfully");
                response.setData(null);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to save POD");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] savePod: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] savePod: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }
}

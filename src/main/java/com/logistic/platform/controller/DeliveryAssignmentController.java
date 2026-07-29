package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.DeliveryAssignment;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.delivery.DeliveryAssignmentRequestDto;
import com.logistic.platform.dto.delivery.DeliveryAssignmentSummaryDto;
import com.logistic.platform.service.DeliveryAssignmentService;
import com.logistic.platform.vo.DeliveryAssignmentSummaryVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/delivery-assignment")
public class DeliveryAssignmentController {

    private static final Logger LOGGER = LogManager.getLogger(DeliveryAssignmentController.class);

    private final DeliveryAssignmentService deliveryAssignmentService;
    private final JdbcTemplate jdbcTemplate;

    public DeliveryAssignmentController(
            DeliveryAssignmentService deliveryAssignmentService,
            @Qualifier("writer") JdbcTemplate jdbcTemplate) {
        this.deliveryAssignmentService = deliveryAssignmentService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createDeliveryAssignment(
            @RequestBody DeliveryAssignmentRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] createDeliveryAssignment: requestDto={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "SYSTEM";

            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] createDeliveryAssignment: assignedBy={}|itemCount={}|driverId={}",
                    requestId, username,
                    requestDto.getConsItemIds() != null ? requestDto.getConsItemIds().size() : 0,
                    requestDto.getDriverId());

            if (requestDto.getConsItemIds() == null || requestDto.getConsItemIds().isEmpty()) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to assign driver");
                response.setData(null);
                return ResponseEntity.ok(response);
            }

            // ── Save one assignment per item ──
            for (Long consItemId : requestDto.getConsItemIds()) {

                Item item = new Item();
                item.setId(consItemId);

                User driver = new User();
                driver.setId(requestDto.getDriverId());

                DeliveryAssignment assignment = new DeliveryAssignment();
                assignment.setItem(item);
                assignment.setDriver(driver);
                assignment.setAssignedBy(username);
                assignment.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "ACTIVE");
                assignment.setAssignedDatetime(LocalDateTime.now());
                assignment.setCreatedDate(LocalDateTime.now());
                assignment.setUpdatedDate(LocalDateTime.now());

                Long generatedId = deliveryAssignmentService
                        .saveDeliveryAssignment(assignment, requestId);

                if (generatedId == null) {
                    LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createDeliveryAssignment: failed for consItemId={}",
                            requestId, consItemId);
                    response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    response.setResponseMessage("Failed to assign driver");
                    response.setData(null);
                    return ResponseEntity.ok(response);
                }

                LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] createDeliveryAssignment: assigned consItemId={}|generatedId={}",
                        requestId, consItemId, generatedId);
            }

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Driver assigned successfully");
            response.setData(null);

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to assign driver");
            response.setData(null);

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] createDeliveryAssignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] createDeliveryAssignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<ResponseDto<DeliveryAssignmentSummaryDto>> getSummary(
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getSummary", requestId);

        ResponseDto<DeliveryAssignmentSummaryDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            // Service returns VO — DTO built here at REST boundary only
            DeliveryAssignmentSummaryVo summaryVo =
                    deliveryAssignmentService.getSummary(requestId);

            if (summaryVo == null) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to get summary");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Summary retrieved successfully");
                response.setData(new DeliveryAssignmentSummaryDto(summaryVo));
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get summary");
            response.setData(null);

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getSummary: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] getSummary: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}")
    public ResponseEntity<ResponseDto<Void>> updateDeliveryAssignment(
            @PathVariable("driverId") Long driverId,
            @RequestParam("consItemId") Long consItemId,
            @RequestParam("status") String status,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] updateDeliveryAssignment: driverId={}|consItemId={}|status={}",
                requestId, driverId, consItemId, status);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] updateDeliveryAssignment: usernameFromAuthHeader={}",
                    requestId, auth.getName());

            Boolean isUpdated = deliveryAssignmentService
                    .updateDeliveryAssignment(driverId, consItemId, status, requestId);

            if (Boolean.TRUE.equals(isUpdated)) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Updated delivery assignment successfully");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to update delivery assignment");
                response.setData(null);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to update delivery assignment");
            response.setData(null);

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] updateDeliveryAssignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] updateDeliveryAssignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }
}
package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.*;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.vo.TrackingConsignmentVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/public/v1/consignment")
public class ConsignmentPublicController {

    private static final Logger LOGGER = LogManager.getLogger(ConsignmentPublicController.class);

    private final ConsignmentService service;

    public ConsignmentPublicController(ConsignmentService service) {
        this.service = service;
    }


    @GetMapping("/track/{consignmentId}")
    public ResponseEntity<ResponseDto<List<TrackingConsignmentResponseDto>>> publicTrackConsignment(
            @PathVariable("consignmentId") String consignmentId,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] publicTrackConsignment: consignmentId={}",
                requestId, consignmentId);

        ResponseDto<List<TrackingConsignmentResponseDto>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] publicTrackConsignment: usernameFromAuthHeader={}", requestId, auth.getName());

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

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] publicTrackConsignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] publicTrackConsignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

}

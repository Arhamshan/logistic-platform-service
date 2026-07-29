package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.Pod;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.consignment.*;
import com.logistic.platform.dto.item.ItemProcessDto;
import com.logistic.platform.dto.pod.PodRequestDto;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.PodService;
import com.logistic.platform.util.ConsignmentValidationUtil;
import com.logistic.platform.vo.ConsignmentVo;
import com.logistic.platform.vo.ItemProcessResultVo;
import com.logistic.platform.vo.SummaryVo;
import com.logistic.platform.vo.TrackingConsignmentVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "SYSTEM";
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] createConsignment: usernameFromAuthHeader={}", requestId, username);

            Consignment consignment = requestDto.getConsignment();

            if(consignment != null) {
                consignment.setCreatedBy(auth.getName());
            }

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
                    List<ItemProcessResultVo> results = service.save(consignment, requestId, username);

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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] trackConsignment: usernameFromAuthHeader={}", requestId, auth.getName());

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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] getSummary: usernameFromAuthHeader={}", requestId, auth.getName());

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

        LOGGER.info("START [REST-LAYER] [RequestId={}] savePod: consignmentId={}|itemId={}|requestDto={}",
                requestId, consignmentId, itemId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Pod pod = new Pod();
            pod.setReceivedBy(requestDto.getReceivedBy());
            pod.setReceiverContact(requestDto.getReceiverContact());
            pod.setRemarks(requestDto.getRemarks());
            pod.setPodPath(requestDto.getPodImage());
            pod.setDeliveredBy(auth.getName());
            pod.setCreatedDate(LocalDateTime.now());
            pod.setCreatedBy(auth.getName());
            pod.setUpdatedDate(LocalDateTime.now());
            pod.setUpdatedBy(auth.getName());

            // 1. Parse the UTC string into an Instant
            Instant instant = Instant.parse(requestDto.getReceivedAt());
            // 2. Convert to LocalDateTime using the system's default timezone
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            pod.setDeliveredAt(localDateTime);

            Boolean saved = podService.savePod(consignmentId, itemId, pod, requestDto.getPodImage(),  requestId);

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

    @GetMapping
    public ResponseEntity<ResponseDto<GetAllConsignmentsResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getAll: pageNumber={}|pageSize={}|sortBy={}|sortDir={}",
                requestId, pageNumber, pageSize, sortBy, sortDir);

        ResponseDto<GetAllConsignmentsResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] getAll: usernameFromAuthHeader={}", requestId, auth.getName());

            List<Consignment> consignments = service.getAllConsignments(pageNumber, pageSize, sortBy, sortDir, requestId);

            Integer countOfAllConsignments = service.getCountOfAllConsignments(requestId);

            if (consignments != null && !consignments.isEmpty()) {

                List<GetAllConsignmentsDto> consignmentDtos = consignments.stream()
                        .map(GetAllConsignmentsDto::new).collect(Collectors.toList());

                GetAllConsignmentsResponseDto responseDto = new GetAllConsignmentsResponseDto(countOfAllConsignments, consignmentDtos);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Consignments retrieved successfully");
                response.setData(responseDto);

            } else {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Consignments not found");
            }

        } catch (Exception e) {

            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get consignments");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getAll: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {

            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] getAll: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteConsignment(
            @PathVariable("id") Long id,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] deleteConsignment: id={}",
                requestId, id);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] deleteConsignment: usernameFromAuthHeader={}", requestId, auth.getName());

            Boolean isDeleted = service.deleteById(id, requestId);

            if (Boolean.TRUE.equals(isDeleted)) {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Consignment deleted successfully.");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to delete consignment.");
                response.setData(null);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to delete consignment.");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] deleteConsignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] deleteConsignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<GetConsignmentResponseDto>> getConsignmentById(
            @PathVariable("id") Long id,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getConsignmentById: id={}",
                requestId, id);

        ResponseDto<GetConsignmentResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] getConsignmentById: usernameFromAuthHeader={}", requestId, auth.getName());

            ConsignmentVo consignment = service.getById(id, requestId);

            if (consignment == null) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Consignment not found");
                response.setData(null);

            } else {
                GetConsignmentResponseDto dto = new GetConsignmentResponseDto(consignment);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Consignment retrieved successfully");
                response.setData(dto);
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get consignment");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getConsignmentById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());

            LOGGER.info("END [REST-LAYER] [RequestId={}] getConsignmentById: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<UpdateConsignmentResponseDto>> updateConsignment(
            @PathVariable("id") Long id,
            @RequestBody UpdateConsignmentRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] updateConsignment: id={}", requestId, id);

        ResponseDto<UpdateConsignmentResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "SYSTEM";
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] updateConsignment: usernameFromAuthHeader={}", requestId, username);

            if (requestDto.getSender() == null && requestDto.getDestination() == null
                    && (requestDto.getItems() == null || requestDto.getItems().isEmpty())) {

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("At least one of sender, destination, or items is required");
                response.setData(null);

            } else {
                Consignment consignment = new Consignment();

                consignment.setConsignmentId(requestDto.getConsignmentId());
                consignment.setSenderContact(requestDto.getSender().getContact());
                consignment.setDestinationContact(requestDto.getDestination().getContact());

                List<Item> items = requestDto.getItems().stream()
                        .map(dto -> {
                            Item item = new Item();
                            item.setId(dto.getId());
                            item.setItemId(dto.getItemId());
                            item.setWeight(dto.getWeight());
                            item.setHeight(dto.getHeight());
                            item.setLength(dto.getLength());
                            item.setWidth(dto.getWidth());

                            String locationToSet = dto.getCurrentLocationCode() != null
                                    ? dto.getCurrentLocationCode()
                                    : requestDto.getLocationCode();
                            item.setCurrentLocationCode(locationToSet);

                            item.setUpdatedBy(username);
                            return item;
                        })
                        .collect(Collectors.toList());

                consignment.setItems(items);

                ConsignmentVo updated = service.updateConsignment(id, consignment, requestId, username);

                if (updated == null) {
                    response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                    response.setResponseMessage("Consignment not found for id " + id);
                    response.setData(null);

                } else {
                    response.setResponseCode(HttpStatus.OK.value());
                    response.setResponseMessage("Consignment updated successfully.");
                    response.setData(new UpdateConsignmentResponseDto(updated));
                }
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to update consignment.");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] updateConsignment: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] updateConsignment: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }
}

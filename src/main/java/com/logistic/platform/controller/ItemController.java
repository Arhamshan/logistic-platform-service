package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.Pod;
import com.logistic.common.enums.EventType;
import com.logistic.common.enums.ItemStatus;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.dto.item.GetItemResponseDto;
import com.logistic.platform.dto.pod.PodResponseDto;
import com.logistic.platform.service.PodService;
import com.logistic.platform.util.ConsignmentUtil;
import com.logistic.platform.dto.item.ScanItemRequestDto;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private static final Logger LOGGER = LogManager.getLogger(ItemController.class);

    private final ItemService itemService;
    private final EventService eventService;
    private final PodService podService;

    public ItemController(ItemService itemService, EventService eventService, PodService podService) {
        this.itemService = itemService;
        this.eventService = eventService;
        this.podService = podService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ResponseDto<Void>> scanItem(
            @RequestBody ScanItemRequestDto requestDto,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] scanItem: requestDto={}",
                requestId, CommonUtils.convertToString(requestDto));

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] scanItem: usernameFromAuthHeader={}", requestId, auth.getName());

            // 1. Fetch item by barcode
            Item item = itemService.getItemByBarcodeNumber(requestDto.getBarcode(), requestId);

            // 2. Build event
            Event event = new Event();
            event.setItem(item);
            event.setEventType(ConsignmentUtil.getEventTypeByItemStatus(ItemStatus.valueOf(requestDto.getStatus())));
            event.setEventLocationCode(requestDto.getLocationCode());
            event.setCreatedBy(requestDto.getScannedBy());

            // 3. createEvent → updates item, consignment, saves event
            eventService.createEvent(event, requestId);

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Item scanned successfully");
            response.setData(null);

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error in scanning item");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] scanItem: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] scanItem: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{status}/{id}")
    public ResponseEntity<ResponseDto<Void>> updateStatus(
            @PathVariable("status") String status,
            @PathVariable("id") Long id,
            @RequestParam("locationCode") String locationCode,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] updateStatus: status={}|id={}|locationCode={}",
                requestId, status, id, locationCode);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "SYSTEM";
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] updateStatus: usernameFromAuthHeader={}", requestId, username);

            Boolean isUpdated = itemService.updateStatus(id, status, locationCode, requestId, username);

            if (Boolean.TRUE.equals(isUpdated)) {

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Status updated successfully");

            } else {

                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Failed to update status");
            }

            response.setData(null);

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Error in updating status");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] updateStatus: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] updateStatus: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{consItemId}/pod")
    public ResponseEntity<ResponseDto<PodResponseDto>> getPod(
            @PathVariable("consItemId") Long consItemId,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getPod: consItemId={}",
                requestId, consItemId);

        ResponseDto<PodResponseDto> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            // Service returns Pod entity — DTO built here at REST boundary only
            Pod pod = podService.getPodByItemId(consItemId, requestId);

            if (pod == null) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Pod not found");
                response.setData(null);

            } else {
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Pod retrieved successfully");
                response.setData(new PodResponseDto(pod));
            }

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("Failed to get pod");

            LOGGER.error("ERROR [REST-LAYER] [RequestId={}] getPod: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] getPod: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{status}")
    public ResponseEntity<ResponseDto<List<GetItemResponseDto>>> getItemsByStatus(
            @PathVariable("status") String status,
            @RequestParam("requestId") String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [REST-LAYER] [RequestId={}] getItemsByStatus: status={}",
                requestId, status);

        ResponseDto<List<GetItemResponseDto>> response = new ResponseDto<>();
        response.setRequestId(requestId);

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            LOGGER.info("DETAIL [REST-LAYER] [RequestId={}] getItemsByStatus: usernameFromAuthHeader={}",
                    requestId, auth.getName());

            List<Item> items = itemService.getItemsByStatus(status, requestId);

            if (items == null || items.isEmpty()) {
                response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                response.setResponseMessage("Items not found");
                response.setData(null);

            } else {
                // Entity → DTO conversion at REST boundary only
                List<GetItemResponseDto> data = items.stream()
                        .map(GetItemResponseDto::new)
                        .collect(Collectors.toList());

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Items retrieved successfully");
                response.setData(data);
            }

        } catch (IllegalArgumentException e) {
            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
            response.setResponseMessage("Invalid item status: " + status);
            response.setData(null);

            LOGGER.warn("WARN [REST-LAYER] [RequestId={}] getItemsByStatus: invalid status={}",
                    requestId, status);

        } finally {
            response.setTimestamp(LocalDateTime.now());
            LOGGER.info("END [REST-LAYER] [RequestId={}] getItemsByStatus: response={}|timeTaken={}",
                    requestId, response, CommonUtils.getExecutionTime(startTime));
        }

        return ResponseEntity.ok(response);
    }
}
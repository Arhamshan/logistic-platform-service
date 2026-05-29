package com.logistic.platform.controller;

import com.logistic.common.dto.ResponseDto;
import com.logistic.common.entity.Event;
import com.logistic.common.entity.Item;
import com.logistic.common.entity.Consignment;
import com.logistic.common.enums.EventType;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.util.ConsignmentUtil;
import com.logistic.platform.dto.item.ScanItemRequestDto;
import com.logistic.platform.service.ConsignmentService;
import com.logistic.platform.service.EventService;
import com.logistic.platform.service.ItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/items")
public class ItemController {

    private static final Logger LOGGER = LogManager.getLogger(ItemController.class);

    private final ItemService itemService;
    private final EventService eventService;
    private final ConsignmentService consignmentService;

    public ItemController(ItemService itemService, EventService eventService, ConsignmentService consignmentService) {
        this.itemService = itemService;
        this.eventService = eventService;
        this.consignmentService = consignmentService;
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
            // 1. Fetch item by barcode
            Item item = itemService.getItemByBarcodeNumber(requestDto.getBarcode(), requestId);

            // 2. Build event
            Event event = new Event();
            event.setItem(item);
            event.setEventType(EventType.valueOf(requestDto.getStatus()));
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
            // 1. Update item status and location — returns item with consignment attached
            Item item = itemService.updateStatusAndLocationById(id, status, locationCode, requestId);
            item.setUpdatedBy("SYSTEM");

            // 2. Update consignment status
            EventType eventType = EventType.valueOf(status);
            Consignment consignment = item.getConsignment();
            consignment.setStatus(ConsignmentUtil.mapConsignmentStatus(eventType));
            consignment.setUpdatedDate(LocalDateTime.now());
            consignment.setUpdatedBy("SYSTEM");
            consignmentService.updateStatus(consignment, requestId);

            // 3. Save event
            Event event = new Event();
            event.setItem(item);
            event.setEventType(eventType);
            event.setEventLocationCode(locationCode);
            event.setDescription(eventType.name());
            event.setCreatedBy("SYSTEM");
            event.setUpdatedBy("SYSTEM");
            eventService.saveEvent(event, requestId);

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Status updated successfully");
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
}
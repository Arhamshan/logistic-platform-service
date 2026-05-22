package com.logistic.platform.dto.item;

import com.logistic.platform.dto.event.TrackingEventResponseDto;
import com.logistic.platform.dto.location.LocationResponseDto;
import com.logistic.platform.vo.ItemProcessResultVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemProcessDto {
    String itemId;

    String consignmentId;

    Integer statusCode;

    String message;

    public ItemProcessDto(ItemProcessResultVo itemProcessResultVo) {
        this.itemId = itemProcessResultVo.getItemId();
        this.consignmentId = itemProcessResultVo.getConsignmentId();
        this.statusCode = itemProcessResultVo.getStatusCode();
        this.message = itemProcessResultVo.getMessage();
    }
}
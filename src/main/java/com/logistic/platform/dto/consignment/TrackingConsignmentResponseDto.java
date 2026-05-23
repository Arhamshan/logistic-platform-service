package com.logistic.platform.dto.consignment;

import com.logistic.platform.dto.item.TrackingItemResponseDto;
import com.logistic.platform.vo.TrackingConsignmentVo;
import com.logistic.platform.vo.TrackingItemVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingConsignmentResponseDto {

    private String consignmentId;

    private String status;

    private List<TrackingItemResponseDto> items;

    public TrackingConsignmentResponseDto(TrackingConsignmentVo trackingConsignmentVo) {
        this.consignmentId = trackingConsignmentVo.getConsignmentId();
        this.status = trackingConsignmentVo.getStatus();
        this.items = trackingConsignmentVo.getItems() != null
                ? trackingConsignmentVo.getItems().stream()
                .map(TrackingItemResponseDto::new)
                .collect(Collectors.toList())
                : null;
    }
}
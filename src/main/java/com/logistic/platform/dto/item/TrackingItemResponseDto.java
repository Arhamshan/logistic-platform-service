package com.logistic.platform.dto.item;

import com.logistic.platform.dto.event.TrackingEventResponseDto;
import com.logistic.platform.dto.location.LocationResponseDto;
import com.logistic.platform.vo.TrackingItemVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingItemResponseDto {

    private Long id;

    private String itemId;

    private String status;

    private String barcode;

    private Double weight;

    private Double height;

    private Double width;

    private Double length;

    private LocationResponseDto currentLocation;

    private List<TrackingEventResponseDto> tracking;

    private String currentLocationCode;

    public TrackingItemResponseDto(TrackingItemVo vo) {
        this.id = vo.getId();
        this.itemId = vo.getItemId();
        this.status = vo.getStatus();
        this.barcode = vo.getBarcode();
        this.weight = vo.getWeight() != null ? vo.getWeight() : null;
        this.height = vo.getHeight() != null ? vo.getHeight() : null;
        this.width  = vo.getWidth()  != null ? vo.getWidth() : null;
        this.length = vo.getLength() != null ? vo.getLength() : null;
        //this.currentLocationCode = vo.getCurrentLocationCode();
        this.currentLocation = vo.getCurrentLocation() != null
                ? new LocationResponseDto(
                vo.getCurrentLocation().getName(),
                vo.getCurrentLocationCode())
                : null;
        this.tracking = vo.getTracking() != null
                ? vo.getTracking().stream()
                .map(TrackingEventResponseDto::new)
                .collect(Collectors.toList())
                : null;
    }
}
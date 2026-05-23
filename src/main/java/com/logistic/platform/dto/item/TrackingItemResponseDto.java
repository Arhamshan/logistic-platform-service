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

    private String itemId;

    private String status;

    private LocationResponseDto currentLocation;

    private List<TrackingEventResponseDto> tracking;

    private String currentLocationCode;

    public TrackingItemResponseDto(TrackingItemVo vo) {
        this.itemId = vo.getItemId();
        this.status = vo.getStatus();
        this.currentLocationCode = vo.getCurrentLocationCode();
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
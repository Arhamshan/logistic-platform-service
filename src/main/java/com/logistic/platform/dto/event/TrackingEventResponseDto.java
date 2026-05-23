package com.logistic.platform.dto.event;

import com.logistic.platform.dto.location.LocationResponseDto;
import com.logistic.platform.vo.TrackingEventVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventResponseDto {

    private String eventType;

    private String description;

    private String eventTime;

    private LocationResponseDto location;

    public TrackingEventResponseDto(TrackingEventVo vo) {
        this.eventType = vo.getEventType();
        this.description = vo.getDescription();
        this.eventTime = vo.getEventTime();
        this.location = vo.getLocation() != null
                ? new LocationResponseDto(
                vo.getLocation().getName(),
                vo.getLocation().getCurrentLocation())
                : null;
    }
}

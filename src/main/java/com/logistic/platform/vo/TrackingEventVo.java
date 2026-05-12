package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingEventVo {

    @JsonProperty("eventType")
    private String eventType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("eventTime")
    private String eventTime;

    @JsonProperty("location")
    private TrackingLocationVo location;
}
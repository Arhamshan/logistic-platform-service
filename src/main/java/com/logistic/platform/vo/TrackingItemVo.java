package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TrackingItemVo {

    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("currentLocation")
    private TrackingLocationVo currentLocation;

    @JsonProperty("tracking")
    private List<TrackingEventVo> tracking;

    @JsonIgnore
    private String currentLocationCode;

}
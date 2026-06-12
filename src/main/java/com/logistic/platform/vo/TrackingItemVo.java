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

    @JsonProperty("barcode_number")
    private String barcode;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("height")
    private Double height;

    @JsonProperty("width")
    private Double width;

    @JsonProperty("length")
    private Double length;

    @JsonProperty("currentLocation")
    private TrackingLocationVo currentLocation;

    @JsonProperty("tracking")
    private List<TrackingEventVo> tracking;

    @JsonIgnore
    private String currentLocationCode;

}
package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrackingLocationVo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("locationCode")
    private String currentLocation;

    public TrackingLocationVo(String name, String name1, String locationCode) {
        this.name = name;
        this.type = name1;
        this.currentLocation = locationCode;
    }
}
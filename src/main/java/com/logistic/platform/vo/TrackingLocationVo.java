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

    public TrackingLocationVo(String name, String name1) {
        this.name = name;
        this.type = name1;
    }
}
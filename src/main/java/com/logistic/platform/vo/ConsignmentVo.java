package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ConsignmentVo {

    @JsonProperty("consignmentId")
    private String consignmentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("items")
    private List<ConsignmentItemVo> items;
}
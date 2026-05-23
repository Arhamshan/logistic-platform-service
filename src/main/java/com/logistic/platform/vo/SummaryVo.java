package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SummaryVo {

    @JsonProperty("totalConsignments")
    private Long totalConsignments;

    @JsonProperty("booked")
    private Long booked;

    @JsonProperty("pickedUp")
    private Long pickedUp;

    @JsonProperty("inTransit")
    private Long inTransit;

    @JsonProperty("outForDelivery")
    private Long outForDelivery;

    @JsonProperty("delivered")
    private Long delivered;

    @JsonProperty("todayBookings")
    private Long todayBookings;
}
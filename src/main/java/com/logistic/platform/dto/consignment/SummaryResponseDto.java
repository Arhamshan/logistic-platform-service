package com.logistic.platform.dto.consignment;

import com.logistic.platform.vo.SummaryVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponseDto {

    private Long totalConsignments;

    private Long booked;

    private Long pickedUp;

    private Long inTransit;

    private Long outForDelivery;

    private Long delivered;

    private Long todayBookings;

    public SummaryResponseDto(SummaryVo summaryVo) {
        this.totalConsignments = summaryVo.getTotalConsignments();
        this.booked = summaryVo.getBooked();
        this.pickedUp = summaryVo.getPickedUp();
        this.inTransit = summaryVo.getInTransit();
        this.outForDelivery = summaryVo.getOutForDelivery();
        this.delivered = summaryVo.getDelivered();
        this.todayBookings = summaryVo.getTodayBookings();
    }
}
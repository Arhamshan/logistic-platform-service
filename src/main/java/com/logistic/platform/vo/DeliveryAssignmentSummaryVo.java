package com.logistic.platform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignmentSummaryVo {

    private Long totalIntransitItems;
    private Long todayAssignments;
    private Long totalDrivers;
    private Long totalPendingAssignment;

    public DeliveryAssignmentSummaryVo(Long totalIntransitItems, Long todayAssignments, Long totalDrivers) {
        this.totalIntransitItems = totalIntransitItems;
        this.todayAssignments = todayAssignments;
        this.totalDrivers = totalDrivers;
    }
}
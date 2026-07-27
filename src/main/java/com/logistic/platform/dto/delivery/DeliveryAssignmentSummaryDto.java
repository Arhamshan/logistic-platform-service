package com.logistic.platform.dto.delivery;

import com.logistic.platform.vo.DeliveryAssignmentSummaryVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAssignmentSummaryDto {

    private Long totalIntransitItems;
    private Long todayAssignments;
    private Long totalDrivers;
    private Long totalPendingAssignment;

    // VO → DTO conversion at REST boundary only
    public DeliveryAssignmentSummaryDto(DeliveryAssignmentSummaryVo vo) {
        this.totalIntransitItems = vo.getTotalIntransitItems();
        this.todayAssignments    = vo.getTodayAssignments();
        this.totalDrivers        = vo.getTotalDrivers();
        this.totalPendingAssignment = vo.getTotalPendingAssignment();
    }
}
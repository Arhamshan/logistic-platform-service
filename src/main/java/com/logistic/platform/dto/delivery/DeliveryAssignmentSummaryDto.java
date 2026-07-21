package com.logistic.platform.dto.delivery;

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
}
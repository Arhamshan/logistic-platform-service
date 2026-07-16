package com.logistic.platform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeliveryAssignmentRequestDto {
    private List<Long> consItemIds;
    private Long driverId;
    private String status;
}
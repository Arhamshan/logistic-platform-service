package com.logistic.platform.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDto {
    private String consignmentId;
    private String itemId;
    private String eventType;
    private String locationCode;
    private String description;
}
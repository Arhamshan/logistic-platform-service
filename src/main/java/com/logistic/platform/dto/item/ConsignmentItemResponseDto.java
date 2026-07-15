package com.logistic.platform.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsignmentItemResponseDto {

    private String itemId;

    private String status;

    private String currentLocationCode;
}
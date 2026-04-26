package com.logistic.platform.dto.consignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsignmentResponseDto {

    private String consignmentId;

    private String locationCode;

    private Integer statusCode;

    private String message;

}

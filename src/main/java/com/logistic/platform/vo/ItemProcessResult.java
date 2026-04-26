package com.logistic.platform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemProcessResult {
    String itemId;

    String consignmentId;

    Integer statusCode;

    String message;
}

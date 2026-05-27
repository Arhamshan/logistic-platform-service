package com.logistic.platform.dto.item;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScanItemRequestDto {

    private String barcode;
    private String status;
    private String locationCode;
    private String scannedBy;
}
package com.logistic.platform.dto.consignment;

import com.logistic.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllConsignmentsItemDto {

    private String itemId;
    private String barcode;
    private Double weight;
    private Double height;
    private Double width;
    private Double length;

    public GetAllConsignmentsItemDto(Item item) {
        this.itemId = item.getItemId();
        this.barcode = item.getBarcodeNumber();
        this.weight = item.getWeight() != null ? Double.valueOf(String.valueOf(item.getWeight())) : null;
        this.height = item.getHeight() != null ? Double.valueOf(String.valueOf(item.getHeight())) : null;
        this.width = item.getWidth() != null ? Double.valueOf(String.valueOf(item.getWidth())) : null;
        this.length = item.getLength() != null ? Double.valueOf(String.valueOf(item.getLength())) : null;
    }
}

package com.logistic.platform.dto.item;

import com.logistic.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetItemResponseDto {

    private String itemId;
    private String status;
    private String currentLocationCode;

    public GetItemResponseDto(Item item) {
        this.itemId              = item.getItemId();
        this.status              = item.getStatus() != null ? item.getStatus().name() : null;
        this.currentLocationCode = item.getCurrentLocationCode();
    }
}
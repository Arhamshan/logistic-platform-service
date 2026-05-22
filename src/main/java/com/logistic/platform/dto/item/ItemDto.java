package com.logistic.platform.dto.item;

import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private String itemId;

    private Float height;

    private Float weight;

    private Float width;

    private Float length;

    public Item getItem(ItemStatus status, String locationCode) {
        Item item = new Item();

        item.setItemId(this.itemId);
        item.setHeight(this.height);
        item.setWeight(this.weight);
        item.setWidth(this.width);
        item.setLength(this.length);
        item.setStatus(status);
        item.setCurrentLocationCode(locationCode);

        return item;
    }
}

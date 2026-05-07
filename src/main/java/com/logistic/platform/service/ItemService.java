package com.logistic.platform.service;


import com.logistic.common.entity.Item;

import java.util.List;

public interface ItemService {

    // Save item
    Boolean save(Item item, String requestId);

    Item getItemByConsignmentIdAndItemId(String consignmentId, String itemId, String requestId);

    void updateStatusAndLocation(Item item, String requestId);
}
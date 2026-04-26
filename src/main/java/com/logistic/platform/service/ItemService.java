package com.logistic.platform.service;


import com.logistic.common.entity.Item;

public interface ItemService {

    // Save item
    Boolean save(Item item, String requestId);

}
package com.logistic.platform.repository;


import com.logistic.common.entity.Item;

import java.util.Optional;

public interface ItemRepository {

    public default Long save(Item item, String requestId){
        return null;
    };

    public default Optional<Item> findByItemId(String itemId, String consignmentId, String requestId){
        return Optional.empty();
    };

    public default Item update(Item item, String requestId){
        return null;
    };

}
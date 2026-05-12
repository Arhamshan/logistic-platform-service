package com.logistic.platform.repository;


import com.logistic.common.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    public default Long save(Item item, String requestId){
        return null;
    };

    public default Optional<Item> findByConsignmentIdAndItemId(String itemId, String consignmentId, String requestId){
        return Optional.empty();
    };

    public default Boolean updateItemStatusAndLocation(Item item, String requestId){
        return false;
    };

    public default List<Item> findItemsByConsignmentId(String consignmentId, String requestId){
        return null;
    }

}
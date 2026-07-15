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

    public default String findLastBarcodeNumber(String requestId){
        return null;
    }

    public default Optional<Item> findById(Long id, String requestId){
        return Optional.empty();
    }

    public default Optional<Item> findByBarcodeNumber(String barcodeNumber, String requestId) {
        return Optional.empty();
    }

    public default List<Item> findByConsId(Long consId, String requestId){
        return null;
    }

    public default Boolean updateItems(List<Item> items, String username, String requestId){
        return false;
    }

    public default List<Item> findAllByStatus(String status, String requestId){
        return null;
    }

    public default List<Item> findAllByDriverId(Long driverId, String requestId){
        return null;
    }
}
package com.logistic.platform.repository;


import com.logistic.common.entity.Item;

public interface ItemRepository {

    Long save(Item item, String requestId);

}
package com.logistic.platform.service;


import com.logistic.common.entity.Item;
import com.logistic.platform.vo.TrackingItemVo;

import java.util.List;

public interface ItemService {

    // Save item
    Boolean save(Item item, String requestId);

    Item getItemByConsignmentIdAndItemId(String consignmentId, String itemId, String requestId);

    void updateStatusAndLocation(Item item, String requestId);

    List<TrackingItemVo> getTrackingItems(String consignmentId, String requestId);

    String generateBarcodeNumber(String lastBarcode,String requestId);

    String getLastBarcodeNumber(String requestId);

    Item getItemByBarcodeNumber(String barcodeNumber, String requestId);

    Boolean updateStatus(Long id, String status, String locationCode, String requestId);
}
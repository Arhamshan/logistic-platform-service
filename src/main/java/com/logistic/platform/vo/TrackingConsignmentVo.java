package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TrackingConsignmentVo {

    @JsonProperty("consignmentId")
    private String consignmentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("items")
    private List<TrackingItemVo> items;

    public TrackingConsignmentVo() {}

    public String getConsignmentId() { return consignmentId; }
    public void setConsignmentId(String consignmentId) { this.consignmentId = consignmentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TrackingItemVo> getItems() { return items; }
    public void setItems(List<TrackingItemVo> items) { this.items = items; }
}
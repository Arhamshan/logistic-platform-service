package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TrackingItemVo {

    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("current_location")
    private TrackingLocationVo currentLocation;

    @JsonProperty("tracking")
    private List<TrackingEventVo> tracking;

    @JsonIgnore
    private String currentLocationCode;

    public TrackingItemVo() {}

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public TrackingLocationVo getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(TrackingLocationVo currentLocation) { this.currentLocation = currentLocation; }

    public List<TrackingEventVo> getTracking() { return tracking; }
    public void setTracking(List<TrackingEventVo> tracking) { this.tracking = tracking; }

    public String getCurrentLocationCode() { return currentLocationCode; }
    public void setCurrentLocationCode(String currentLocationCode) { this.currentLocationCode = currentLocationCode; }
}
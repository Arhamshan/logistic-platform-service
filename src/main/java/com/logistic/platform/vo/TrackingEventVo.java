package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrackingEventVo {

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("event_time")
    private String eventTime;

    @JsonProperty("location")
    private TrackingLocationVo location;

    public TrackingEventVo() {}

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public TrackingLocationVo getLocation() { return location; }
    public void setLocation(TrackingLocationVo location) { this.location = location; }
}
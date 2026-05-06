package com.logistic.platform.util;

import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.EventType;
import com.logistic.common.enums.ItemStatus;

public class ConsignmentUtil {

    public static EventType getEventTypeByItemStatus(ItemStatus status) {
        EventType eventType = switch (status) {
            case BOOKED -> EventType.PARCEL_BOOKED;
            case PICKED_UP -> EventType.PARCEL_PICKED_UP;
            case IN_TRANSIT -> EventType.PARCEL_IN_TRANSIT;
            case OUT_FOR_DELIVERY -> EventType.PARCEL_OUT_FOR_DELIVERY;
            case DELIVERED -> EventType.PARCEL_DELIVERED;
        };

        return eventType;
    }

    public static ItemStatus mapItemStatus(EventType eventType) {
        switch (eventType) {
            case PARCEL_BOOKED:            return ItemStatus.BOOKED;
            case PARCEL_PICKED_UP:         return ItemStatus.PICKED_UP;
            case PARCEL_IN_TRANSIT:        return ItemStatus.IN_TRANSIT;
            case PARCEL_OUT_FOR_DELIVERY:  return ItemStatus.OUT_FOR_DELIVERY;
            case PARCEL_DELIVERED:         return ItemStatus.DELIVERED;
            default: throw new IllegalArgumentException("Unknown eventType: " + eventType);
        }
    }

    public static ConsignmentStatus mapConsignmentStatus(EventType eventType) {
        switch (eventType) {
            case PARCEL_BOOKED:            return ConsignmentStatus.BOOKED;
            case PARCEL_PICKED_UP:         return ConsignmentStatus.PICKED_UP;
            case PARCEL_IN_TRANSIT:        return ConsignmentStatus.IN_TRANSIT;
            case PARCEL_OUT_FOR_DELIVERY:  return ConsignmentStatus.OUT_FOR_DELIVERY;
            case PARCEL_DELIVERED:         return ConsignmentStatus.DELIVERED;
            default: throw new IllegalArgumentException("Unknown eventType: " + eventType);
        }
    }
}

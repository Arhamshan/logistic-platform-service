package com.logistic.platform.util;

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
}

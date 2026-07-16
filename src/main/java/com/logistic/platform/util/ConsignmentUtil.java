package com.logistic.platform.util;

import com.logistic.common.enums.ConsignmentStatus;
import com.logistic.common.enums.EventType;
import com.logistic.common.enums.ItemStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConsignmentUtil {

    public static EventType getEventTypeByItemStatus(ItemStatus status) {
        EventType eventType = switch (status) {
            case BOOKED -> EventType.PARCEL_BOOKED;
            case PICKED_UP -> EventType.PARCEL_PICKED_UP;
            case IN_TRANSIT -> EventType.PARCEL_IN_TRANSIT;
            case OUT_FOR_DELIVERY -> EventType.PARCEL_OUT_FOR_DELIVERY;
            case DELIVERED -> EventType.PARCEL_DELIVERED;

            default -> throw new IllegalArgumentException("Unknown status: " + status);
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

    public static ConsignmentStatus deriveConsignmentStatusFromItems(List<ItemStatus> itemStatuses) {

        if (itemStatuses == null || itemStatuses.isEmpty()) {
            return ConsignmentStatus.BOOKED;
        }

        int total = itemStatuses.size();

        long booked    = count(itemStatuses, ItemStatus.BOOKED);
        long pickedUp  = count(itemStatuses, ItemStatus.PICKED_UP);
        long inTransit = count(itemStatuses, ItemStatus.IN_TRANSIT);
        long outForDel = count(itemStatuses, ItemStatus.OUT_FOR_DELIVERY);
        long delivered = count(itemStatuses, ItemStatus.DELIVERED);

        // ── Rule 1: uniform status — direct map ──
        if (delivered == total) return ConsignmentStatus.DELIVERED;
        if (outForDel == total) return ConsignmentStatus.OUT_FOR_DELIVERY;
        if (inTransit == total) return ConsignmentStatus.IN_TRANSIT;
        if (pickedUp  == total) return ConsignmentStatus.PICKED_UP;
        if (booked    == total) return ConsignmentStatus.BOOKED;

        // ── Rule 2: any DELIVERED but not all → PARTIALLY_DELIVERED ──
        // (delivered takes priority — it's the most "final" state a client cares about)
        if (delivered > 0) return ConsignmentStatus.PARTIALLY_DELIVERED;

        // ── Rule 3: furthest-along progress state, mixed but none delivered yet ──
        // Walk the pipeline from the end backwards — whichever stage has at least
        // one item AND isn't the full set, becomes the partial status.
        if (outForDel > 0) return ConsignmentStatus.PARTIALLY_OUT_FOR_DELIVERY;
        if (inTransit > 0) return ConsignmentStatus.PARTIALLY_IN_TRANSIT;
        if (pickedUp  > 0) return ConsignmentStatus.PARTIALLY_PICKED_UP;

        // ── Fallback ──
        return ConsignmentStatus.BOOKED;
    }

    private static long count(List<ItemStatus> statuses, ItemStatus target) {
        return statuses.stream().filter(s -> s == target).count();
    }
}

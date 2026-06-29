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

    /**
     * Derives the consignment-level status based on ALL item statuses.
     *
     * Rules (evaluated in priority order):
     *  1. If all items share the same status → direct map to that consignment status.
     *  2. If any item is DELIVERED but not all → PARTIALLY_DELIVERED.
     *  3. If any item is OUT_FOR_DELIVERY / IN_TRANSIT / PICKED_UP but not all → PARTIALLY_PICKED_UP.
     *  4. Fallback → BOOKED.
     *
     * @param itemStatuses list of current statuses for every item in the consignment
     * @return the derived ConsignmentStatus
     */
    public static ConsignmentStatus deriveConsignmentStatusFromItems(List<ItemStatus> itemStatuses) {

        if (itemStatuses == null || itemStatuses.isEmpty()) {
            return ConsignmentStatus.BOOKED;
        }

        Set<ItemStatus> distinctStatuses = itemStatuses.stream()
                .collect(Collectors.toSet());

        // Rule 1 — All items have the same status
        if (distinctStatuses.size() == 1) {
            ItemStatus singleStatus = distinctStatuses.iterator().next();
            switch (singleStatus) {
                case BOOKED:            return ConsignmentStatus.BOOKED;
                case PICKED_UP:         return ConsignmentStatus.PICKED_UP;
                case IN_TRANSIT:        return ConsignmentStatus.IN_TRANSIT;
                case OUT_FOR_DELIVERY:  return ConsignmentStatus.OUT_FOR_DELIVERY;
                case DELIVERED:         return ConsignmentStatus.DELIVERED;
            }
        }

        // Rule 2 — At least one item DELIVERED but not all
        boolean anyDelivered = itemStatuses.stream()
                .anyMatch(s -> s == ItemStatus.DELIVERED);

        if (anyDelivered) {
            return ConsignmentStatus.PARTIALLY_DELIVERED;
        }

        // Rule 3 — At least one item picked up / in-transit / out-for-delivery but not all
        boolean anyInProgress = itemStatuses.stream()
                .anyMatch(s -> s == ItemStatus.PICKED_UP
                        || s == ItemStatus.IN_TRANSIT
                        || s == ItemStatus.OUT_FOR_DELIVERY);

        if (anyInProgress) {
            return ConsignmentStatus.PARTIALLY_PICKED_UP;
        }

        // Rule 4 — Fallback
        return ConsignmentStatus.BOOKED;
    }
}

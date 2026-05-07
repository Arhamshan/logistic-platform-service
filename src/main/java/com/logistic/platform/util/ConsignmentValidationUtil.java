package com.logistic.platform.util;

import com.logistic.platform.dto.ContactDto;
import com.logistic.platform.dto.consignment.CreateConsignmentRequestDto;
import com.logistic.platform.dto.consignment.ItemDto;

public class ConsignmentValidationUtil {

    public static String validate(CreateConsignmentRequestDto dto) {

        if (isBlank(dto.getConsignmentId()))
            return "Invalid consignmentId";

        if (isBlank(dto.getLocationCode()))
            return "Invalid locationCode";

        if (dto.getItems() == null || dto.getItems().isEmpty())
            return "Item is required";

        for (int i = 0; i < dto.getItems().size(); i++) {
            ItemDto item = dto.getItems().get(i);
            String itemError = validateItem(item, i + 1);
            if (itemError != null)
                return itemError;
        }

        if (dto.getSender() == null)
            return "Invalid sender";

        String senderError = validateContact(dto.getSender(), "sender");
        if (senderError != null)
            return senderError;

        if (dto.getDestination() == null)
            return "Invalid destination";

        String destinationError = validateContact(dto.getDestination(), "destination");
        if (destinationError != null)
            return destinationError;

        return null;
    }

    private static String validateContact(ContactDto contact, String prefix) {

        if (isBlank(contact.getName()))
            return "Invalid " + prefix + " name";

        if (isBlank(contact.getEmail()))
            return "Invalid " + prefix + " email";

        if (isBlank(contact.getPhone()))
            return "Invalid " + prefix + " phone";

        if (isBlank(contact.getAddressLine1()))
            return "Invalid " + prefix + " addressLine1";

        if (isBlank(contact.getSuburb()))
            return "Invalid " + prefix + " suburb";

        if (isBlank(contact.getState()))
            return "Invalid " + prefix + " state";

        if (isBlank(contact.getPostCode()))
            return "Invalid " + prefix + " postCode";

        if (isBlank(contact.getCountry()))
            return "Invalid " + prefix + " country";

        return null;
    }

    private static String validateItem(ItemDto item, int index) {

        if (item == null)
            return "Invalid item at position " + index;

        if (isBlank(item.getItemId()))
            return "Invalid itemId at position " + index;

        if (item.getHeight() == null)
            return "Invalid height at position " + index;

        if (item.getWeight() == null)
            return "Invalid weight at position " + index;

        if (item.getWidth() == null)
            return "Invalid width at position " + index;

        if (item.getLength() == null)
            return "Invalid length at position " + index;

        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
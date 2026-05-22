package com.logistic.platform.dto.consignment;

import com.logistic.common.entity.Consignment;
import com.logistic.common.entity.Contact;
import com.logistic.common.entity.Item;
import com.logistic.common.enums.ItemStatus;
import com.logistic.platform.dto.ContactDto;
import com.logistic.platform.dto.item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsignmentRequestDto {

    private String consignmentId;

    private String locationCode;

    private List<ItemDto> items;

    private ContactDto sender;

    private ContactDto destination;

    public Consignment getConsignment() {

        final List<Item> itemList = items.stream().map(i -> i.getItem(ItemStatus.BOOKED, locationCode)).collect(Collectors.toList());

        Contact senderContact = this.sender.getContact();
        Contact destinationContact = this.destination.getContact();

        Consignment consignment = new Consignment();

        consignment.setConsignmentId(consignmentId);
        consignment.setItems(itemList);
        consignment.setSenderContact(senderContact);
        consignment.setDestinationContact(destinationContact);

        return consignment;
    }

}

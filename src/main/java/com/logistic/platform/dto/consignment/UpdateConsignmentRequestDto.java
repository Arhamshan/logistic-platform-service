package com.logistic.platform.dto.consignment;

import com.logistic.platform.dto.ContactDto;
import com.logistic.platform.dto.item.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConsignmentRequestDto {

    private ContactDto sender;

    private ContactDto destination;

    private List<ItemDto> items;

    private String locationCode;

    private String consignmentId;

}

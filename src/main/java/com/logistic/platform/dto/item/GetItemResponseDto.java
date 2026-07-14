package com.logistic.platform.dto.item;

import com.logistic.common.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetItemResponseDto {

    private String itemId;
    private String status;
    private String itemWeight;
    private String currentLocationCode;

    // Destination contact fields
    private String destinationContactName;
    private String destinaionAddressLine1;
    private String destinaionAddressLine2;
    private String destinaionState;
    private String destinaionSuburb;
    private String destinaionPostCode;
    private String destinaionCountry;

    public GetItemResponseDto(Item item) {
        this.itemId              = item.getItemId();
        this.status              = item.getStatus() != null ? item.getStatus().name() : null;
        this.itemWeight          = item.getWeight() != null ? String.valueOf(item.getWeight()) : null;
        this.currentLocationCode = item.getCurrentLocationCode();

        // Destination contact via consignment → destinationContact
        if (item.getConsignment() != null
                && item.getConsignment().getDestinationContact() != null) {
            var dest = item.getConsignment().getDestinationContact();
            this.destinationContactName = dest.getName();
            this.destinaionAddressLine1 = dest.getAddressLine1();
            this.destinaionAddressLine2 = dest.getAddressLine2();
            this.destinaionState        = dest.getState();
            this.destinaionSuburb       = dest.getSuburb();
            this.destinaionPostCode     = dest.getPostcode();
            this.destinaionCountry      = dest.getCountry();
        }
    }
}
package com.logistic.platform.dto.consignment;

import com.logistic.common.entity.Consignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllConsignmentsDto {

    private Long id;

    private String consignmentId;

    private String status;

    private String locationCode;

    private String senderContactName;

    private String senderContactAddressLine1;

    private String senderContactAddressLine2;

    private String senderContactState;

    private String senderContactSuburb;

    private String senderContactPostcode;

    private String senderContactCountry;

    private String destinationContactName;

    private String destinationContactAddressLine1;

    private String destinationContactAddressLine2;

    private String destinationContactState;

    private String destinationContactSuburb;

    private String destinationContactPostcode;

    private String destinationContactCountry;

    public GetAllConsignmentsDto(Consignment consignment) {
        this.id = consignment.getId();
        this.consignmentId = consignment.getConsignmentId();
        this.status = consignment.getConsignmentId();
        this.locationCode = consignment.getCurrentLocationCode();
        this.senderContactName = consignment.getSenderContact().getName();
        this.senderContactAddressLine1 = consignment.getSenderContact().getAddressLine1();
        this.senderContactAddressLine2 = consignment.getSenderContact().getAddressLine2();
        this.senderContactState = consignment.getSenderContact().getState();
        this.senderContactSuburb = consignment.getSenderContact().getSuburb();
        this.senderContactPostcode = consignment.getSenderContact().getPostcode();
        this.senderContactCountry = consignment.getSenderContact().getCountry();
        this.destinationContactName = consignment.getDestinationContact().getName();
        this.destinationContactAddressLine1 = consignment.getDestinationContact().getAddressLine1();
        this.destinationContactAddressLine2 = consignment.getDestinationContact().getAddressLine2();
        this.destinationContactState = consignment.getDestinationContact().getState();
        this.destinationContactSuburb = consignment.getDestinationContact().getSuburb();
        this.destinationContactPostcode = consignment.getDestinationContact().getPostcode();
        this.destinationContactCountry = consignment.getDestinationContact().getCountry();
    }
}

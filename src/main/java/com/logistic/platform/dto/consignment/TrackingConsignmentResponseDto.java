package com.logistic.platform.dto.consignment;

import com.logistic.platform.dto.item.TrackingItemResponseDto;
import com.logistic.platform.vo.TrackingConsignmentVo;
import com.logistic.platform.vo.TrackingItemVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingConsignmentResponseDto {

    private String consignmentId;
    private String status;
    private String locationCode;
    private List<TrackingItemResponseDto> items;

    // Sender
    private String senderContactName;
    private String senderContactEmail;
    private String senderContactPhone;
    private String senderContactAddressLine1;
    private String senderContactAddressLine2;
    private String senderContactState;
    private String senderContactSuburb;
    private String senderContactPostcode;
    private String senderContactCountry;

    // Destination
    private String destinationContactName;
    private String destinationContactEmail;
    private String destinationContactPhone;
    private String destinationContactAddressLine1;
    private String destinationContactAddressLine2;
    private String destinationContactState;
    private String destinationContactSuburb;
    private String destinationContactPostcode;
    private String destinationContactCountry;

    public TrackingConsignmentResponseDto(TrackingConsignmentVo vo) {
        this.consignmentId = vo.getConsignmentId();
        this.status = vo.getStatus();
        this.locationCode = vo.getLocationCode();
        this.items = vo.getItems() != null
                ? vo.getItems().stream().map(TrackingItemResponseDto::new).collect(Collectors.toList())
                : null;

        this.senderContactName = vo.getSenderContactName();
        this.senderContactEmail = vo.getSenderContactEmail();
        this.senderContactPhone = vo.getSenderContactPhone();
        this.senderContactAddressLine1 = vo.getSenderContactAddressLine1();
        this.senderContactAddressLine2 = vo.getSenderContactAddressLine2();
        this.senderContactState = vo.getSenderContactState();
        this.senderContactSuburb = vo.getSenderContactSuburb();
        this.senderContactPostcode = vo.getSenderContactPostcode();
        this.senderContactCountry = vo.getSenderContactCountry();

        this.destinationContactName = vo.getDestinationContactName();
        this.destinationContactEmail = vo.getDestinationContactEmail();
        this.destinationContactPhone = vo.getDestinationContactPhone();
        this.destinationContactAddressLine1 = vo.getDestinationContactAddressLine1();
        this.destinationContactAddressLine2 = vo.getDestinationContactAddressLine2();
        this.destinationContactState = vo.getDestinationContactState();
        this.destinationContactSuburb = vo.getDestinationContactSuburb();
        this.destinationContactPostcode = vo.getDestinationContactPostcode();
        this.destinationContactCountry = vo.getDestinationContactCountry();
    }
}
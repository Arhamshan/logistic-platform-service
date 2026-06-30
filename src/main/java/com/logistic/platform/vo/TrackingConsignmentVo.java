package com.logistic.platform.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TrackingConsignmentVo {

    @JsonProperty("consignmentId")
    private String consignmentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("locationCode")
    private String locationCode;

    @JsonProperty("items")
    private List<TrackingItemVo> items;

    // ── Sender ──
    @JsonProperty("senderContactName")
    private String senderContactName;
    @JsonProperty("senderContactEmail")
    private String senderContactEmail;
    @JsonProperty("senderContactPhone")
    private String senderContactPhone;
    @JsonProperty("senderContactAddressLine1")
    private String senderContactAddressLine1;
    @JsonProperty("senderContactAddressLine2")
    private String senderContactAddressLine2;
    @JsonProperty("senderContactState")
    private String senderContactState;
    @JsonProperty("senderContactSuburb")
    private String senderContactSuburb;
    @JsonProperty("senderContactPostcode")
    private String senderContactPostcode;
    @JsonProperty("senderContactCountry")
    private String senderContactCountry;

    // ── Destination ──
    @JsonProperty("destinationContactName")
    private String destinationContactName;
    @JsonProperty("destinationContactEmail")
    private String destinationContactEmail;
    @JsonProperty("destinationContactPhone")
    private String destinationContactPhone;
    @JsonProperty("destinationContactAddressLine1")
    private String destinationContactAddressLine1;
    @JsonProperty("destinationContactAddressLine2")
    private String destinationContactAddressLine2;
    @JsonProperty("destinationContactState")
    private String destinationContactState;
    @JsonProperty("destinationContactSuburb")
    private String destinationContactSuburb;
    @JsonProperty("destinationContactPostcode")
    private String destinationContactPostcode;
    @JsonProperty("destinationContactCountry")
    private String destinationContactCountry;
}
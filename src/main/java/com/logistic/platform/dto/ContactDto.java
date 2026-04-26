package com.logistic.platform.dto;

import com.logistic.common.entity.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

    private String name;

    private String email;

    private String phone;

    private String addressLine1;

    private String addressLine2;

    private String suburb;

    private String state;

    private String postCode;

    private String country;

    public Contact getContact() {
        Contact contact = new Contact();

        contact.setName(this.name);
        contact.setEmail(this.email);
        contact.setPhone(this.phone);
        contact.setAddressLine1(this.addressLine1);
        contact.setAddressLine2(this.addressLine2);
        contact.setSuburb(this.suburb);
        contact.setState(this.state);
        contact.setPostcode(this.postCode);
        contact.setCountry(this.country);

        return contact;
    }
}

package com.logistic.platform.dto.user;

import com.logistic.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponseDto {

    private Long id;
    private String username;
    private String name;
    private String contactNo;
    private String address1;
    private String address2;
    private String suburb;
    private String state;
    private String country;

    public GetUserResponseDto(User user) {
        this.id        = user.getId();
        this.username  = user.getUsername();
        this.name      = user.getContact() != null ? user.getContact().getName()  : null;
        this.contactNo = user.getContact() != null ? user.getContact().getPhone() : null;
        this.address1   = user.getContact() != null ? user.getContact().getAddressLine1() : null;
        this.address2   = user.getContact() != null ? user.getContact().getAddressLine2() : null;
        this.suburb    = user.getContact() != null ? user.getContact().getSuburb() : null;
        this.state     = user.getContact() != null ? user.getContact().getState() : null;
        this.country   = user.getContact() != null ? user.getContact().getCountry() : null;
    }
}
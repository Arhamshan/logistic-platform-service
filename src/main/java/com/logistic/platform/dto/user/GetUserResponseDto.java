package com.logistic.platform.dto.user;

import com.logistic.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponseDto {

    private String username;
    private String name;
    private String contactNo;
    private String address;

    public GetUserResponseDto(User user) {
        this.username  = user.getUsername();
        this.name      = user.getContact() != null ? user.getContact().getName()  : null;
        this.contactNo = user.getContact() != null ? user.getContact().getPhone() : null;
        this.address   = user.getContact() != null ? user.getContact().getAddressLine1() : null;
    }
}
package com.logistic.platform.dto.user;

import com.logistic.common.entity.Contact;
import com.logistic.common.entity.User;

import com.logistic.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    private String username;
    private String password;
    private String role;

    // Contact fields — collected at registration
    private String name;
    private String email;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String suburb;
    private String state;
    private String postcode;
    private String country;

    public User toUser() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRole(Role.valueOf(this.role.toUpperCase()));

        if (this.name != null || this.phone != null || this.email != null) {
            Contact contact = new Contact();
            contact.setName(this.name);
            contact.setEmail(this.email);
            contact.setPhone(this.phone);
            contact.setAddressLine1(this.addressLine1);
            contact.setAddressLine2(this.addressLine2);
            contact.setSuburb(this.suburb);
            contact.setState(this.state);
            contact.setPostcode(this.postcode);
            contact.setCountry(this.country);
            user.setContact(contact);
        }

        return user;
    }
}
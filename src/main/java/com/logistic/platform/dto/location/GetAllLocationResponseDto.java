package com.logistic.platform.dto.location;

import com.logistic.common.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllLocationResponseDto {
    private Long id;

    private String name;

    private String locationCode;

    private String country;

    private String city;

    private String type;

    private String latitude;

    private String longitude;


    public GetAllLocationResponseDto(Location location) {
        this.id = location.getId();
        this.name = location.getName();
        this.locationCode = location.getLocationCode();
        this.country = location.getCountry();
        this.city = location.getCity();
        this.type = location.getType().name();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
}

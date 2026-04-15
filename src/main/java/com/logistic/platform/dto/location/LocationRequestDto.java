package com.logistic.platform.dto.location;

import com.logistic.common.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDto {
    private String name;

    private String type;

    private String city;

    private String country;

    private String latitude;

    private String longitude;

    public Location toLocation() {
        Location loc = new Location();

        loc.setName(this.name);
        loc.setType(this.type);
        loc.setCity(this.city);
        loc.setCountry(this.country);
        loc.setLatitude(this.latitude);
        loc.setLongitude(this.longitude);

        return loc;
    }
}

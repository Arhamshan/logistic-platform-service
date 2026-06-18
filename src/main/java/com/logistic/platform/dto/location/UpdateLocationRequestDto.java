package com.logistic.platform.dto.location;

import com.logistic.common.entity.Location;
import com.logistic.common.enums.LocationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequestDto {

    private Long id;
    private String name;
    private String type;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
    private String updatedBy;

    // Add this method to convert DTO to Entity
    public Location toLocation() {
        Location location = new Location();
        location.setId(this.id);
        location.setName(this.name);
        location.setType(LocationType.valueOf(this.type));
        location.setCity(this.city);
        location.setCountry(this.country);
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        location.setUpdatedBy(this.updatedBy);
        return location;
    }
}
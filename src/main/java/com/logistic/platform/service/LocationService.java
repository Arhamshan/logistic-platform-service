package com.logistic.platform.service;

import com.logistic.common.entity.Location;
import java.util.List;

public interface LocationService {

    public Boolean create(Location location, String requestId);

    public String generateLocationCode(String requestId);

    List<Location> getAllLocations(String requestId);

    Boolean updateLocation(Location location, String requestId);

    Location getLocationByCode(String locationCode, String requestId);

    Boolean deleteById(Long id, String requestId);
}

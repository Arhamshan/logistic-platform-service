package com.logistic.platform.repository;

import com.logistic.common.entity.Location;
import com.logistic.platform.exception.LocationCreateException;

import java.util.List;

public interface LocationRepository {

    public default Long save(Location location, String requestId) throws LocationCreateException {
        return null;
    }

    public default String findLastLocationCode(String requestId) {
        return null;
    }

    public default List<Location> findAllLocations(String requestId){
        return null;
    };
}

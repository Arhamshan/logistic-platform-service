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

    public default List<Location> findAllLocations(int pageNumber, int pageSize, String sortBy, String sortDir, Boolean isGetAllLocations, String requestId){
        return null;
    };

    public default Integer findAllLocationsCount(String requestId) {
        return null;
    }

    public default Boolean update(Location location, String requestId){
        return false;
    };

    public default Location findByLocationCode(String locationCode, String requestId){
        return null;
    };

    public default Boolean deleteById(Long id, String requestId){
        return false;
    };

}

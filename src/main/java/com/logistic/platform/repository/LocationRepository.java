package com.logistic.platform.repository;

import com.logistic.common.entity.Location;
import com.logistic.platform.exception.LocationCreateException;

public interface LocationRepository {

    public default Long save(Location location, String requestId) throws LocationCreateException {
        return null;
    }

    public default String findLastLocationCode(String requestId) {
        return null;
    }

}

package com.logistic.platform.service;

import com.logistic.common.entity.Location;

public interface LocationService {

    public Boolean create(Location location, String requestId);

    public String generateLocationCode(String requestId);

}

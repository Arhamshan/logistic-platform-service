package com.logistic.platform.service.impl;

import com.logistic.common.entity.Location;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.LocationReaderRepository;
import com.logistic.platform.repository.writer.LocationWriterRepository;
import com.logistic.platform.service.LocationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Logger LOGGER = LogManager.getLogger(LocationServiceImpl.class);

    final LocationWriterRepository writerRepository;
    final LocationReaderRepository readerRepository;

    public LocationServiceImpl(LocationWriterRepository writerRepository,
                               LocationReaderRepository readerRepository) {

        this.writerRepository = writerRepository;
        this.readerRepository = readerRepository;

    }

    @Override
    public Boolean create(Location location, String requestId) {
        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] save: location={} ", CommonUtils.convertToString(location), requestId);

        boolean isCreated = Boolean.FALSE;

        try {

            Long locationId = writerRepository.save(location, requestId);

            if (locationId != null) {
                isCreated = Boolean.TRUE;
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] save: Ex={}|Trace={}", requestId,
                    e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] save: isCreated={}|timeTaken={}", requestId,
                    isCreated, CommonUtils.getExecutionTime(startTime));
        }

        return isCreated;

    }

    @Override
    public String generateLocationCode(String requestId) {
        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] generateLocationCode: ", requestId);

        String newLocationCode = null;

        String lastLocationCode = readerRepository.findLastLocationCode(requestId);

        if (CommonUtils.isBlankString(lastLocationCode)) {

            newLocationCode = "LOC0000001";

        } else {

            String prefix = lastLocationCode.substring(0, 3);
            int number = Integer.parseInt(lastLocationCode.substring(3));

            number++;

            newLocationCode = prefix + String.format("%07d", number);

        }

        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] generateLocationCode: newLocationCode={}|timeTaken={}", requestId,
                newLocationCode, CommonUtils.getExecutionTime(startTime));

        return newLocationCode;

    }

    @Override
    public List<Location> getAllLocations(int pageNumber, int pageSize, String sortBy, String sortDir, Boolean isGetAllLocations, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getAllLocations: pageNumber={}|pageSize={}|sortBy={}|sortDir={}|isGetAllLocations={}",
                requestId, pageNumber, pageSize, sortBy, sortDir, isGetAllLocations);

        List<Location> locationList = null;

        try {

            locationList = readerRepository.findAllLocations(pageNumber, pageSize, sortBy, sortDir, isGetAllLocations, requestId);
        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getAllLocations: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getAllLocations: size={}|timeTaken={}",
                    requestId,
                    locationList != null ? locationList.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return  locationList != null ? locationList : List.of();
    }

    @Override
    public Integer getCountOfAllLocations(String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getCountOfAllLocations:", requestId);

        Integer allCount = readerRepository.findAllLocationsCount(requestId);


        LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getCountOfAllLocations: timeTaken={}",
                requestId, CommonUtils.getExecutionTime(startTime));

        return allCount;
    }

    @Override
    public Boolean updateLocation(Location location, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] updateLocation: location={}",
                requestId, CommonUtils.convertToString(location));

        boolean isUpdated = false;

        try {

            isUpdated = writerRepository.update(location, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] updateLocation: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] updateLocation: isUpdated={}|timeTaken={}",
                    requestId, isUpdated, CommonUtils.getExecutionTime(startTime));
        }

        return isUpdated;
    }

    @Override
    public Location getLocationByCode(String locationCode, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getLocationByCode: locationCode={}",
                requestId, locationCode);

        Location location = null;

        try {
            // repository returns single Location directly
            location = readerRepository.findByLocationCode(locationCode, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getLocationByCode: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getLocationByCode: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }

        return location;
    }

    @Override
    public Boolean deleteById(Long id, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] deleteById: id={}",
                requestId, id);

        Boolean isDeleted = Boolean.FALSE;

        try {
            isDeleted = writerRepository.deleteById(id, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] deleteById: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] deleteById: isDeleted={}|timeTaken={}",
                    requestId, isDeleted, CommonUtils.getExecutionTime(startTime));
        }

        return isDeleted;
    }
}

package com.logistic.platform.service.impl;

import com.logistic.common.entity.Location;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.LocationReaderRepository;
import com.logistic.platform.repository.writer.LocationWriterRepository;
import com.logistic.platform.service.LocationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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
}

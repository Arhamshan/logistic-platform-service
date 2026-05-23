package com.logistic.platform.service.impl;

import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.common.enums.Status;
import com.logistic.platform.repository.reader.UserReaderRepository;
import com.logistic.platform.repository.writer.UserWriterRepository;
import com.logistic.platform.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private final UserWriterRepository writerRepository;

    private final UserReaderRepository readerRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserWriterRepository writerRepository,
                           UserReaderRepository readerRepository,
                           PasswordEncoder passwordEncoder) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
        this.readerRepository = readerRepository;
    }

    @Override
    public Boolean createUser(User user, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createUser", requestId);

        boolean isCreated = false;

        try {

            // 🔐 Hash password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // ✅ Set status (String for now)
            user.setStatus(Status.ACTIVE);

            isCreated = writerRepository.save(user, requestId);

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createUser: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createUser: isCreated={} | timeTaken={}",
                    requestId, isCreated, CommonUtils.getExecutionTime(startTime));
        }

        return isCreated;
    }

    @Override
    public User getByUsername(String username, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getByUsername", requestId);

        User user = null;

        try {

            user = readerRepository.findByUsername(username, requestId);

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getByUsername: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());

            throw e;

        } finally {

            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getByUsername: timeTaken={}",
                    requestId, CommonUtils.getExecutionTime(startTime));
        }

        return user;
    }
}
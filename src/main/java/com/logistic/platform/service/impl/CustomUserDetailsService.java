package com.logistic.platform.service.impl;

import com.logistic.common.entity.User;
import com.logistic.common.util.CommonUtils;
import com.logistic.platform.repository.reader.UserReaderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LogManager.getLogger(CustomUserDetailsService.class);

    private final UserReaderRepository readerRepository;

    public CustomUserDetailsService(UserReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] loadUserByUsername : username={}", username);

        UserDetails userDetails;

        try {

            User user = readerRepository.findByUsername(username, null);

            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();

        } catch (Exception e) {

            LOGGER.error("ERROR [SERVICE-LAYER] loadUserByUsername: Ex={}|Trace={}",
                    e.getMessage(), e.getStackTrace());

            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] loadUserByUsername: timeTaken={}", CommonUtils.getExecutionTime(startTime));
        }

        return userDetails;
    }
}
package com.logistic.platform.service.impl;

import com.logistic.common.entity.Contact;
import com.logistic.common.entity.User;
import com.logistic.common.enums.Role;
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

import java.util.List;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private final UserWriterRepository writerRepository;

    private final UserReaderRepository readerRepository;

    private final PasswordEncoder passwordEncoder;

    private final ContactServiceImpl contactService;

    public UserServiceImpl(UserWriterRepository writerRepository,
                           UserReaderRepository readerRepository,
                           PasswordEncoder passwordEncoder, ContactServiceImpl contactService) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
        this.readerRepository = readerRepository;
        this.contactService = contactService;
    }

    @Override
    public Boolean createUser(User user, String requestId, String username) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] createUser", requestId);

        boolean isCreated = false;

        try {
            // 1. Save contact first if provided — get generated ID back
            if (user.getContact() != null) {

                Contact contact = user.getContact();
                contact.setCreatedDate(LocalDateTime.now());
                contact.setUpdatedDate(LocalDateTime.now());
                contact.setCreatedBy(user.getUsername());
                contact.setUpdatedBy(user.getUsername());

                Long contactId = contactService.createContact(contact, requestId, username).getId();

                if (contactId == null) {
                    throw new RuntimeException("Failed to save contact for user: " + user.getUsername());
                }

                // Link generated contact id back to user
                Contact linkedContact = new Contact();
                linkedContact.setId(contactId);
                user.setContact(linkedContact);
            }

            // 2. Hash password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 3. Set status
            user.setStatus(Status.ACTIVE);

            // 4. Save user — writerRepository now writes contact_id too
            isCreated = writerRepository.save(user, requestId);

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] createUser: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] createUser: isCreated={}|timeTaken={}",
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

    @Override
    public List<User> getUsersByType(String type, String requestId) {

        long startTime = System.currentTimeMillis();

        LOGGER.info("START [SERVICE-LAYER] [RequestId={}] getUsersByType: type={}",
                requestId, type);

        List<User> result = null;

        try {
            // Validate role exists in enum before querying
            try {
                Role.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid user type: " + type);
            }

            result = readerRepository.findAllByRole(type.toUpperCase(), requestId);

            if (result == null || result.isEmpty()) {
                LOGGER.warn("WARN [SERVICE-LAYER] [RequestId={}] getUsersByType: No users found for type={}",
                        requestId, type);
            }

        } catch (Exception e) {
            LOGGER.error("ERROR [SERVICE-LAYER] [RequestId={}] getUsersByType: Ex={}|Trace={}",
                    requestId, e.getMessage(), e.getStackTrace());
            throw e;

        } finally {
            LOGGER.info("END [SERVICE-LAYER] [RequestId={}] getUsersByType: count={}|timeTaken={}",
                    requestId,
                    result != null ? result.size() : 0,
                    CommonUtils.getExecutionTime(startTime));
        }

        return result;
    }
}
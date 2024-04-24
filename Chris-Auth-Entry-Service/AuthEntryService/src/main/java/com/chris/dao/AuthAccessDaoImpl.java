package com.chris.dao;


import com.chris.entity.AuthUser;
import com.chris.entity.UserStatus;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_DAO_BEAN;

/**
 * dao impl for the auth user access
 */

@Repository(value = AUTH_ACCESS_DAO_BEAN)
public class AuthAccessDaoImpl implements AuthAccessDao {
    private final Logger _LOG = LoggerFactory.getLogger(AuthAccessDaoImpl.class);

    @Value("${app.find.users.page.size:100}")
    private Integer _defaultPageSize;

    private final EntityManager _manager;

    //ToDo: add the test cases and fake sample data via liquibase
    @Autowired
    public AuthAccessDaoImpl(EntityManager manager) {
        _manager = manager;
    }

    @Override
    public List<AuthUser> findAllUsers() {
        return null;
    }

    @Override
    public AuthUser findUserByEmail(String email) {
        return null;
    }

    @Override
    public void saveAuthUser(AuthUser user) {

    }

    @Override
    public void updateUserStatus(UserStatus status) {

    }
}

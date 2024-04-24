package com.chris.dao;

import com.chris.entity.AuthUser;
import com.chris.entity.UserStatus;

import java.util.List;

public interface AuthAccessDao {
    List<AuthUser> findAllUsers();

    AuthUser findUserByEmail(String email);


    void saveAuthUser(AuthUser user);

    void updateUserStatus(UserStatus status);
}

package com.chris.dao;

import com.chris.entity.AuthCommon;
import com.chris.entity.AuthUser;
import com.chris.entity.UserStatus;

import java.util.List;

public interface AuthAccessDao {
    List<AuthUser> findAllUsers();
    List<AuthUser> findAllUsers(int startPage, int pageCount);
    List<AuthUser> findUserByName(String username);
    AuthUser findUserById(Integer id);
    AuthUser findUserByEmail(String email);
    void saveAuthUser(AuthUser user);
    void updateUserRole(AuthUser user, AuthCommon roleType);
    void updateAuthUser(AuthUser user);
    void updateUserStatus(UserStatus status);
    void deleteAuthUser(AuthUser user);
}

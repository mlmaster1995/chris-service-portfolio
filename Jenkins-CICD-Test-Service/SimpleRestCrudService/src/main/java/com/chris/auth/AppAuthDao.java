package com.chris.auth;

public interface AppAuthDao {
    AuthUser findUserByUserName(String userName);
    Role findRoleByRoleName(String roleName);
}

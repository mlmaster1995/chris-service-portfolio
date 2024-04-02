package com.chris.auth;

public interface AppAuthDao {
    AuthUser findByUserName(String userName);
    Role findRoleByRoleName(String roleName);
}

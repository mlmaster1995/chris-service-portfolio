package com.chris.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppAuthService extends UserDetailsService {
    AuthUser findUserByUserName(String userName);
    Role findRoleByRoleName(String roleName);
}

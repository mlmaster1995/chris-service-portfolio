package com.chris.auth;

/**
 * enum type for all roles used by the app
 */
public enum RoleType {
    USER("USER"),
    ADMIN("AMIND"),
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String _roleType;

    RoleType(String role) {
        _roleType = role;
    }

    public String getVal() {
        return _roleType;
    }
}

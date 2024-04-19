package com.chris.entity;

/**
 * enum type for all roles used by the app
 */
public enum RoleType {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String _roleType;

    RoleType(String role) {
        _roleType = role;
    }

    public String getVal() {
        return _roleType;
    }
}

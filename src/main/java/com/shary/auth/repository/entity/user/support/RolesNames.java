package com.shary.auth.repository.entity.user.support;

public enum RolesNames {
    GUEST("ROLE_GUEST"), OWNER("ROLE_OWNER"), RENTER("ROLE_RENTER"),
    DBA("ROLE_DBA"), ADMIN("ROLE_ADMIN"), MODERATOR("ROLE_MODERATOR");

    public final String name;

    RolesNames(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

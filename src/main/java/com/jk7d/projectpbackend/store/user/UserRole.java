package com.jk7d.projectpbackend.store.user;

public enum UserRole {
    ADMIN,
    USER;


    public static UserRole fromValue(final String value) {
        return UserRole.valueOf(value);
    }
}


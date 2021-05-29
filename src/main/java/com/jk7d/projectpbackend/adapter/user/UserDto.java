package com.jk7d.projectpbackend.adapter.user;

import java.util.UUID;

public record UserDto(UUID id, String username, String email) {

    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String email() {
        return this.email;
    }
}

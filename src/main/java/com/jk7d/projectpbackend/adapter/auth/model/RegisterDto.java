package com.jk7d.projectpbackend.adapter.auth.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request Body for a register POST request
 */
public record RegisterDto(@NotNull @NotBlank @Size(max = 255) @Email String email,
                          @NotNull @NotBlank @Size(min = 5, max = 255) String username,
                          @NotNull @NotBlank @Size(max = 64) String password) {
    @Override
    public String email() {
        return this.email;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public String password() {
        return this.password;
    }
}

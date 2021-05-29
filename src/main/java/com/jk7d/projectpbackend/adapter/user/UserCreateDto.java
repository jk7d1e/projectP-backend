package com.jk7d.projectpbackend.adapter.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UserCreateDto(@NotNull @NotBlank @Size(max = 255) @Email String email,
                            @NotNull @NotBlank @Size(min = 5, max = 255) String username,
                            @NotNull @NotBlank @Size(max = 64) String password,
                            @NotNull @NotBlank boolean isEnabled,
                            @NotNull @NotBlank String role) {
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

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String role() {
        return this.role;
    }
}

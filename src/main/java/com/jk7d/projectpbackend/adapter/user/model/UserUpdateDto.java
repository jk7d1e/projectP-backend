package com.jk7d.projectpbackend.adapter.user.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record UserUpdateDto(@NotNull @NotBlank @Size(max = 255) @Email String email,
                            @NotNull @NotBlank @Size(min = 5, max = 255) String username,
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
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String role() {
        return this.role;
    }
}

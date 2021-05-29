package com.jk7d.projectpbackend.adapter.project.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record ProjectCreateDto(@NotNull @NotBlank @Size(min = 5, max = 50) String name) {

    @Override
    public String name() {
        return this.name;
    }
}

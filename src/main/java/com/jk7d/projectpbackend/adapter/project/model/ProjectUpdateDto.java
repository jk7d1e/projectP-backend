package com.jk7d.projectpbackend.adapter.project.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record ProjectUpdateDto(@NotNull @NotBlank @Size(min = 5, max = 50) String name,
                               @NotNull @NotBlank @Size(max = 50) String description) {

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }
}

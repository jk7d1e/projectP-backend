package com.jk7d.projectpbackend.adapter.task.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record TaskCreateDto(@NotNull @NotBlank @Size(min = 5, max = 50) String title,
                            @NotNull @NotBlank @Size(min = 5, max = 50) String description) {

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String description() {
        return this.description;
    }
}

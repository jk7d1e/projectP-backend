package com.jk7d.projectpbackend.adapter.task.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record TaskCreateDto(
        @NotNull @NotBlank @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$") String projectId,
        @NotNull @NotBlank @Size(min = 5, max = 50) String title,
        @NotNull @NotBlank @Size(min = 5, max = 50) String description) {

    @Override
    public String projectId() {
        return this.projectId();
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String description() {
        return this.description;
    }
}

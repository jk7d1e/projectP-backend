package com.jk7d.projectpbackend.adapter.task.model;

import java.util.UUID;

public record TaskDto(UUID id, String title, String description, String status) {
    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String status() {
        return this.status;
    }
}

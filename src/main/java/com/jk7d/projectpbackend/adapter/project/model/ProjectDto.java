package com.jk7d.projectpbackend.adapter.project.model;

import java.util.List;
import java.util.UUID;

public record ProjectDto(UUID id, String name, String description, String owner, List<String> members) {
    @Override
    public UUID id() {
        return this.id;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String owner() {
        return this.owner;
    }

    @Override
    public List<String> members() {
        return this.members;
    }
}

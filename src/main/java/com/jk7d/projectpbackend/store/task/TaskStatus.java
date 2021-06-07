package com.jk7d.projectpbackend.store.task;

public enum TaskStatus {
    OPEN,
    IN_PROGRESS,
    REVIEWING,
    CLOSED;

    public static TaskStatus fromValue(final String value) {
        return TaskStatus.valueOf(value);
    }
}

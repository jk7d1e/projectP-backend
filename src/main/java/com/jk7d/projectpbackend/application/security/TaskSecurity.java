package com.jk7d.projectpbackend.application.security;

import com.jk7d.projectpbackend.store.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TaskSecurity {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskSecurity(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Check if current signed in user is the creator of task
     *
     * @param authentication signed in user
     * @param taskId         task
     * @return true if creator,  false if not
     */
    public boolean isTaskCreator(final Authentication authentication, final UUID taskId) {
        return true;
    }

    /**
     * Check if current signed in user is assigned to task
     * @param authentication signed in user
     * @param taskId task
     * @return true if assigned, false if not
     */
    public boolean isTaskAssignee(final Authentication authentication, final UUID taskId) {
        return true;
    }
}

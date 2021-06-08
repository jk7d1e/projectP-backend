package com.jk7d.projectpbackend.adapter.task.controller;

import com.jk7d.projectpbackend.adapter.task.model.TaskCreateDto;
import com.jk7d.projectpbackend.adapter.task.model.TaskUpdateDto;
import com.jk7d.projectpbackend.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or @projectSecurity.isProjectMember(authentication, #taskCreateDto.projectId())")
    public ResponseEntity<?> createTask(@Valid @RequestBody final TaskCreateDto taskCreateDto) throws URISyntaxException {
        return this.taskService.createTask(taskCreateDto);
    }

    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN') or @projectSecurity.isProjectMember(authentication, #projectId)")
    public ResponseEntity<?> readTaskById(@RequestParam(name = "pid") final UUID projectId,
                                          @PathVariable final UUID taskId) {
        return this.taskService.readTaskById(projectId, taskId);
    }

    @PutMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@projectSecurity.isProjectMember(authentication, #projectId) and " +
            "(@taskSecurity.isTaskCreator(authentication, #taskId) or @taskSecurity.isTaskAssignee(authentication, #taskId))")
    public ResponseEntity<?> updateTaskById(@PathVariable final UUID projectId, @PathVariable final UUID taskId,
                                            @Valid @RequestBody final TaskUpdateDto taskUpdateDto) {
        return this.taskService.updateTaskById(projectId, taskId, taskUpdateDto);
    }

    @DeleteMapping(value = "/{taskId}")
    @PreAuthorize("@projectSecurity.isProjectMember(authentication, #projectId) and @taskSecurity.isTaskCreator(authentication, #taskId)")
    public ResponseEntity<?> deleteTaskById(@PathVariable final UUID projectId, @PathVariable final UUID taskId) {
        return this.taskService.deleteTaskById(projectId, taskId);
    }
}

package com.jk7d.projectpbackend.service.task;

import com.jk7d.projectpbackend.adapter.task.model.TaskCreateDto;
import com.jk7d.projectpbackend.adapter.task.model.TaskDto;
import com.jk7d.projectpbackend.adapter.task.model.TaskUpdateDto;
import com.jk7d.projectpbackend.service.security.IUserDetails;
import com.jk7d.projectpbackend.store.project.Project;
import com.jk7d.projectpbackend.store.project.ProjectRepository;
import com.jk7d.projectpbackend.store.task.Task;
import com.jk7d.projectpbackend.store.task.TaskRepository;
import com.jk7d.projectpbackend.store.task.TaskStatus;
import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    private final UserRepository userRepository;

    @Autowired
    public TaskService(final TaskRepository taskRepository, final ProjectRepository projectRepository, final UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param projectId
     * @param taskCreateDto
     * @return
     * @throws URISyntaxException
     */
    public ResponseEntity<?> createTask(final UUID projectId, final TaskCreateDto taskCreateDto) throws URISyntaxException {
        final Project project = this.findProject(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }
        final IUserDetails userDetails = (IUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final Optional<User> owner = this.userRepository.findByEmail(userDetails.getEmail());
        if (owner.isPresent()) {
            Task task = new Task(taskCreateDto.title(), taskCreateDto.description(), project, owner.get());
            task = this.taskRepository.save(task);
            return ResponseEntity.created(new URI("/tasks/" + task.getId())).body("Created task");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @param projectId
     * @param taskId
     * @return
     */
    public ResponseEntity<?> readTaskById(final UUID projectId, final UUID taskId) {
        final Project project = this.findProject(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }

        final Optional<Task> task = this.taskRepository.findById(taskId);

        if (task.isPresent()) {
            final Task taskObj = task.get();
            final TaskDto taskDto = new TaskDto(taskObj.getId(), taskObj.getTitle(), taskObj.getDescription(), taskObj.getStatus().name());
            return ResponseEntity.ok().body(taskDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @param projectId
     * @param taskId
     * @param taskUpdateDto
     * @return
     */
    public ResponseEntity<?> updateTaskById(final UUID projectId, final UUID taskId, final TaskUpdateDto taskUpdateDto) {
        final Project project = this.findProject(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }

        final Optional<Task> task = this.taskRepository.findById(taskId);
        if (task.isPresent()) {
            final Task taskObj = this.validateTaskForUpdate(taskUpdateDto, task.get());
            final TaskDto taskDto = new TaskDto(taskObj.getId(), taskObj.getTitle(), taskObj.getDescription(), taskObj.getStatus().name());
            return ResponseEntity.ok().body(taskDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @param projectId
     * @param taskId
     * @return
     */
    public ResponseEntity<?> deleteTaskById(final UUID projectId, final UUID taskId) {
        final Project project = this.findProject(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }
        final Optional<Task> task = this.taskRepository.findById(taskId);
        if (task.isPresent()) {
            this.taskRepository.delete(task.get());
            return ResponseEntity.ok().body("Deleted.");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @param projectId
     * @return
     */
    private Project findProject(final UUID projectId) {
        final Optional<Project> project = this.projectRepository.findById(projectId);
        return project.orElse(null);
    }

    /**
     * @param taskUpdateDto
     * @param task
     * @return
     */
    private Task validateTaskForUpdate(final TaskUpdateDto taskUpdateDto, final Task task) {
        if (!taskUpdateDto.title().equals(task.getTitle())) {
            task.setTitle(taskUpdateDto.title());
        }

        if (!taskUpdateDto.description().equals(task.getDescription())) {
            task.setDescription(taskUpdateDto.description());
        }

        if (!taskUpdateDto.status().equals(task.getStatus().name())) {
            task.setStatus(TaskStatus.valueOf(taskUpdateDto.status()));
        }

        return this.taskRepository.save(task);
    }

}

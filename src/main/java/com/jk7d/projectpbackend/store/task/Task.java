package com.jk7d.projectpbackend.store.task;

import com.jk7d.projectpbackend.store.DateAudit;
import com.jk7d.projectpbackend.store.project.Project;
import com.jk7d.projectpbackend.store.user.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity(name = "tab_tasks")
public class Task extends DateAudit {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", length = 512, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "tab_task_assignees",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<User> assignees;

    public Task(final String title, final String description, final Project project, final User creator) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.creator = creator;
        this.status = TaskStatus.OPEN;
        this.assignees = Collections.emptyList();
    }

    public Task() {

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(final TaskStatus status) {
        this.status = status;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(final Project project) {
        this.project = project;
    }

    public User getCreator() {
        return this.creator;
    }

    public void setCreator(final User creator) {
        this.creator = creator;
    }

    public List<User> getAssignees() {
        return this.assignees;
    }

    public void setAssignees(final List<User> assignees) {
        this.assignees = assignees;
    }
}

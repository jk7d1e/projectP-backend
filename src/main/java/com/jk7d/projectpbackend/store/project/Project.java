package com.jk7d.projectpbackend.store.project;

import com.jk7d.projectpbackend.store.DateAudit;
import com.jk7d.projectpbackend.store.task.Task;
import com.jk7d.projectpbackend.store.user.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Entity(name = "tab_projects")
public class Project extends DateAudit {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinTable(
            name = "tab_project_members",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<User> members;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Project(final String name, final String description, final User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members = Collections.emptyList();
        this.tasks = Collections.emptyList();
    }

    public Project() {

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    public List<User> getMembers() {
        return this.members;
    }

    public void setMembers(final List<User> members) {
        this.members = members;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(final List<Task> tasks) {
        this.tasks = tasks;
    }
}

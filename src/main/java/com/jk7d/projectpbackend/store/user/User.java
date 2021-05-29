package com.jk7d.projectpbackend.store.user;

import com.jk7d.projectpbackend.store.DateAudit;
import com.jk7d.projectpbackend.store.project.Project;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Entity(name = "tab_user")
public class User extends DateAudit {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "username", length = 25, nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 64, nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Project> ownedProjects;

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<Project> memberOfProjects;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Default User creation
     *
     * @param username
     * @param email
     * @param password
     */
    public User(final String username, final String email, final String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = false;
        this.ownedProjects = Collections.emptyList();
        this.memberOfProjects = Collections.emptyList();
        this.role = UserRole.USER;
    }

    public User() {

    }

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(final boolean enabled) {
        this.isEnabled = enabled;
    }

    public List<Project> getOwnedProjects() {
        return this.ownedProjects;
    }

    public void setOwnedProjects(final List<Project> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    public List<Project> getMemberOfProjects() {
        return this.memberOfProjects;
    }

    public void setMemberOfProjects(final List<Project> memberOfProjects) {
        this.memberOfProjects = memberOfProjects;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(final UserRole role) {
        this.role = role;
    }
}

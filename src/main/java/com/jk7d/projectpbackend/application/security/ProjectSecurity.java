package com.jk7d.projectpbackend.application.security;

import com.jk7d.projectpbackend.service.security.IUserDetails;
import com.jk7d.projectpbackend.store.project.Project;
import com.jk7d.projectpbackend.store.project.ProjectRepository;
import com.jk7d.projectpbackend.store.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectSecurity {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectSecurity(final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    /**
     * Check if current authenticated user is the owner of the project with id
     *
     * @param authentication user auth
     * @param projectId      project id
     * @return true if user is owner
     */
    public boolean isProjectOwner(final Authentication authentication, final UUID projectId) {
        final IUserDetails userDetails = (IUserDetails) authentication.getPrincipal();
        final Optional<Project> project = this.projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return false;
        }
        final Project projectObj = project.get();

        return userDetails.getId().equals(projectObj.getOwner().getId());
    }

    /**
     * Check if signed in user is member of project
     *
     * @param authentication signed in user
     * @param projectId      project
     * @return true if member of this project
     */
    public boolean isProjectMember(final Authentication authentication, final UUID projectId) {
        final IUserDetails userDetails = (IUserDetails) authentication.getPrincipal();
        final Optional<Project> project = this.projectRepository.findById(projectId);
        if (project.isEmpty()) {
            return false;
        }
        final Project projectObj = project.get();

        // Check members
        for (final User u : projectObj.getMembers()) {
            return userDetails.getId().equals(u.getId());
        }

        // Owner is also a member
        return userDetails.getId().equals(projectObj.getOwner().getId());
    }
}

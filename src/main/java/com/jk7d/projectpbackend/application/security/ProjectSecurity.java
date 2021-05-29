package com.jk7d.projectpbackend.application.security;

import com.jk7d.projectpbackend.service.security.IUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProjectSecurity {

    /**
     * @param authentication
     * @param projectId
     * @return
     */
    public boolean isProjectOwner(final Authentication authentication, final UUID projectId) {
        final IUserDetails userDetails = (IUserDetails) authentication.getPrincipal();
        return true;
    }

    /**
     * @param authentication
     * @param projectId
     * @return
     */
    public boolean isProjectMember(final Authentication authentication, final UUID projectId) {
        final IUserDetails userDetails = (IUserDetails) authentication.getPrincipal();
        return true;
    }
}

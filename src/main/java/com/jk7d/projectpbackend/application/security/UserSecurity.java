package com.jk7d.projectpbackend.application.security;

import com.jk7d.projectpbackend.service.security.IUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserSecurity {

    /**
     * Check if the current logged in users id matches the get request
     * id parameter
     *
     * @param authentication authenticated user
     * @param userId         path parameter
     * @return true if ids are equal
     */
    public boolean isUser(final Authentication authentication, final UUID userId) {
        final IUserDetails userDetails = (IUserDetails) authentication.getPrincipal();
        return userDetails.getId().equals(userId);
    }

}

package com.jk7d.projectpbackend.application.security;

import com.jk7d.projectpbackend.infrastructure.common.JwtUtil;
import com.jk7d.projectpbackend.infrastructure.constant.JwtType;
import com.jk7d.projectpbackend.service.security.IUserDetails;
import com.jk7d.projectpbackend.service.security.IUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull final HttpServletRequest httpServletRequest, @NotNull final HttpServletResponse httpServletResponse,
                                    @NotNull final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String accessJwt = this.parseJwt(httpServletRequest);
            if (accessJwt != null && this.jwtUtil.validateJwt(accessJwt, JwtType.ACCESS)) {
                // Extract jwt sub field
                final String username = this.jwtUtil.getUsernameFromJwt(accessJwt, JwtType.ACCESS);

                // Build details
                final IUserDetails userDetails = (IUserDetails) this.userDetailsService.loadUserByUsername(username);
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                // Set auth context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (final Exception e) {
            LOGGER.error("Cannot set user authentication: {}", e.getLocalizedMessage());
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Parses the access jwt from the authorization header
     *
     * @param request Incoming http request
     * @return Value extracted from header
     */
    private String parseJwt(final HttpServletRequest request) {
        final String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}

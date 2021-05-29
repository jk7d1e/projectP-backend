package com.jk7d.projectpbackend.service.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jk7d.projectpbackend.store.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class IUserDetails implements UserDetails {

    private final UUID id;

    private final String username;

    private final String email;

    @JsonIgnore
    private final String password;

    private final boolean isEnabled;

    private Collection<? extends GrantedAuthority> authorities;

    public IUserDetails(final UUID id, final String username, final String email, final String password,
                        final boolean isEnabled, final Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
    }

    public static IUserDetails build(final User user) {

        final Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));

        return new IUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public UUID getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}

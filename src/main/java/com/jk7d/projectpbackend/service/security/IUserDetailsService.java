package com.jk7d.projectpbackend.service.security;

import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class IUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    IUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        // spring username will be email
        final User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return IUserDetails.build(user);
    }
}

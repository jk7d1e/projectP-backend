package com.jk7d.projectpbackend.service.user;

import com.jk7d.projectpbackend.adapter.user.UserCreateDto;
import com.jk7d.projectpbackend.adapter.user.UserDto;
import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.UserRepository;
import com.jk7d.projectpbackend.store.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    @Autowired
    public UserService(final UserRepository userRepository, final PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * @param userCreateDto
     * @return
     */
    public ResponseEntity<String> createUser(final UserCreateDto userCreateDto) {
        // Check for email
        if (this.userRepository.existsByEmail(userCreateDto.username())) {
            return new ResponseEntity<>("Error: Email in use.", HttpStatus.BAD_REQUEST);
        }

        // Check for Username
        if (this.userRepository.existsByUsername(userCreateDto.username())) {
            return new ResponseEntity<>("Error: Username in use.", HttpStatus.BAD_REQUEST);
        }

        // Create user
        final User user = new User(userCreateDto.username(),
                userCreateDto.email(), this.encoder.encode(userCreateDto.password()));
        user.setEnabled(userCreateDto.isEnabled());
        user.setRole(UserRole.fromValue(userCreateDto.role()));
        this.userRepository.save(user);

        return ResponseEntity.ok().body("User created");
    }

    /**
     * @param userId
     * @return
     */
    public ResponseEntity<?> readUserById(final UUID userId) {
        final Optional<User> user = this.userRepository.findById(userId);

        if (user.isPresent()) {
            final User userObj = user.get();
            final UserDto userDto = new UserDto(userObj.getId(), userObj.getUsername(), userObj.getEmail());
            return ResponseEntity.ok().body(userDto);
        } else {
            return ResponseEntity.badRequest().body("Error: User " + userId + " not found.");
        }
    }

    /**
     * @param userCreateDto
     * @return
     */
    public ResponseEntity<?> updateUserById(final UUID id, final UserCreateDto userCreateDto) {
        final Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            final User userObj = this.validateUserFieldsForUpdate(userCreateDto, user.get());

            final UserDto userDto = new UserDto(userObj.getId(), userObj.getUsername(), userObj.getEmail());
            return ResponseEntity.ok().body(userDto);
        } else {
            return ResponseEntity.badRequest().body("Error: Project " + id + " not found.");
        }
    }


    /**
     * @param userId
     * @return
     */
    public ResponseEntity<String> deleteUserById(final UUID userId) {
        final Optional<User> user = this.userRepository.findById(userId);
        if (user.isPresent()) {
            this.userRepository.delete(user.get());
            return ResponseEntity.ok().body("Deleted.");
        } else {
            return ResponseEntity.badRequest().body("Error: User " + userId + " not found.");
        }
    }

    /**
     * @param userCreateDto
     * @param user
     * @return
     */
    private User validateUserFieldsForUpdate(final UserCreateDto userCreateDto, final User user) {
        if (!userCreateDto.username().equals(user.getUsername())) {
            user.setUsername(userCreateDto.username());
        }

        if (!userCreateDto.email().equals(user.getEmail())) {
            user.setEmail(userCreateDto.email());
        }

        if (!userCreateDto.isEnabled() == user.isEnabled()) {
            user.setEnabled(userCreateDto.isEnabled());
        }

        if (!userCreateDto.role().equals(user.getRole().name())) {
            user.setRole(UserRole.fromValue(userCreateDto.role()));
        }

        return this.userRepository.save(user);
    }
}

package com.jk7d.projectpbackend.adapter.user;

import com.jk7d.projectpbackend.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@Valid @RequestBody final UserCreateDto userCreateDto) {
        return this.userService.createUser(userCreateDto);
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUser(authentication, #userId)")
    public ResponseEntity<?> readUserById(@PathVariable final UUID userId) {
        return this.userService.readUserById(userId);
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserById(@PathVariable final UUID userId, @Valid @RequestBody final UserCreateDto userCreateDto) {
        return this.userService.updateUserById(userId, userCreateDto);
    }

    @DeleteMapping(value = "/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable final UUID userId) {
        return this.userService.deleteUserById(userId);
    }
}

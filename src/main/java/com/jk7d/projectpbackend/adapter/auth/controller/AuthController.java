package com.jk7d.projectpbackend.adapter.auth.controller;

import com.jk7d.projectpbackend.adapter.auth.model.LoginDto;
import com.jk7d.projectpbackend.adapter.auth.model.RegisterDto;
import com.jk7d.projectpbackend.service.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@Valid @RequestBody final LoginDto loginDto) {
        return this.authService.loginUser(loginDto);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@Valid @RequestBody final RegisterDto registerDto) {
        return this.authService.registerUser(registerDto);
    }

    @PostMapping(value = "/register/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("ct") final UUID ct) {
        return this.authService.confirmRegistration(ct);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout() {
        return this.authService.logout();

    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<String> refresh(@CookieValue(name = "_jid") final String _jid) {
        return this.authService.refresh(_jid);

    }
}

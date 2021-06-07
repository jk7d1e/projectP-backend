package com.jk7d.projectpbackend.service.security;

import com.jk7d.projectpbackend.adapter.auth.model.LoginDto;
import com.jk7d.projectpbackend.adapter.auth.model.RegisterDto;
import com.jk7d.projectpbackend.infrastructure.common.JwtUtil;
import com.jk7d.projectpbackend.infrastructure.constant.JwtType;
import com.jk7d.projectpbackend.service.mail.MailService;
import com.jk7d.projectpbackend.store.user.User;
import com.jk7d.projectpbackend.store.user.UserRepository;
import com.jk7d.projectpbackend.store.user.confirmation.ConfirmationToken;
import com.jk7d.projectpbackend.store.user.confirmation.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Repos
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    // Tools
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    @Value("${jk7d.jwt.refresh.expiration}")
    private int refreshJwtExpiration;
    @Value("${jk7d.confirmation.token.expiration}")
    private int confirmationTokenExpiration;

    @Autowired
    public AuthService(final AuthenticationManager authenticationManager, final UserRepository userRepository,
                       final ConfirmationTokenRepository confirmationTokenRepository, final PasswordEncoder encoder,
                       final JwtUtil jwtUtil, final MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.mailService = mailService;
    }

    /**
     * Registers a new user, also checks if user with email/username already exists
     *
     * @param registerDto Request Body
     * @return Configured response
     */
    public ResponseEntity<String> registerUser(final RegisterDto registerDto) {

        // Check for email
        if (this.userRepository.existsByEmail(registerDto.username())) {
            return new ResponseEntity<>("Error: Email in use.", HttpStatus.BAD_REQUEST);
        }

        // Check for Username
        if (this.userRepository.existsByUsername(registerDto.username())) {
            return new ResponseEntity<>("Error: Username in use.", HttpStatus.BAD_REQUEST);
        }

        // Create user
        final User user = new User(registerDto.username(),
                registerDto.email(), this.encoder.encode(registerDto.password()));
        this.userRepository.save(user);

        // Create confirmation token
        final ConfirmationToken confirmationToken = new ConfirmationToken(user, this.confirmationTokenExpiration);
        this.confirmationTokenRepository.save(confirmationToken);

        // Send confirmation Email
        try {
            this.mailService.sendConfirmationEmail(user, confirmationToken);
        } catch (final MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body("Verification Email sent.");
    }

    /**
     * Confirms the registration of a User based on a query parameter
     *
     * @param token UUID value of the confirmation token
     * @return Configured Response
     */
    public ResponseEntity<String> confirmRegistration(final UUID token) {
        final Optional<ConfirmationToken> confirmationToken = this.confirmationTokenRepository.findByToken(token);

        // Check for token entity
        if (confirmationToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: No token found.");
        }

        final ConfirmationToken confirmationTokenObject = confirmationToken.get();
        final Optional<User> user = this.userRepository.findByEmail(confirmationTokenObject.getUser().getEmail());

        if (user.isPresent()) {
            // Token is not expired yet
            if (new Date().before(confirmationTokenObject.getExpiresAt())) {
                // Set user account to enabled
                final User userObj = user.get();
                userObj.setEnabled(true);
                // delete the not needed token
                this.confirmationTokenRepository.delete(confirmationTokenObject);
                this.userRepository.save(userObj);
                return ResponseEntity.ok().body("User confirmed.");
            } else {
                // Delete token if request was made with expired token
                this.confirmationTokenRepository.delete(confirmationTokenObject);
                return ResponseEntity.badRequest().body("Error: Confirmation Token expired.");
            }
        } else {
            return ResponseEntity.badRequest().body("Error: User not found.");
        }
    }

    /**
     * Authenticates a user, means email and password of a user are exchanged
     * for a set of Json web Tokens
     *
     * @param loginDto Request Body
     * @return Configured Response
     */
    public ResponseEntity<String> loginUser(final LoginDto loginDto) {
        final Optional<User> user = this.userRepository.findByEmail(loginDto.email());
        // Check if user exists
        if (user.isPresent()) {
            final User userObj = user.get();
            // Check if user has confirmed is account registration
            if (userObj.isEnabled()) {
                // Authenticate
                final Authentication authentication = this.authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
                // Tell spring
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Create set of jwt
                final String refreshJwt = this.jwtUtil.generateJwt(authentication, JwtType.REFRESH);
                final String accessJwt = this.jwtUtil.generateJwt(authentication, JwtType.ACCESS);

                // Build cookie
                final ResponseCookie refreshCookie = this.createRefreshJwtCookie(refreshJwt);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).body(accessJwt);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: Email not confirmed");
            }
        } else {
            return ResponseEntity.badRequest().body("Error: No user found");
        }

    }

    /**
     * Logs a user out by deleting the Cookie containing the refresh jwt
     *
     * @return Response containing Cookie
     */
    public ResponseEntity<?> logout() {
        final ResponseCookie expiredCookie = this.resetRefreshJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, expiredCookie.toString()).build();
    }

    /**
     * Use jwt in cookie header to refresh the users access token
     *
     * @param refreshJwt refresh token
     * @return Configured Response
     */
    public ResponseEntity<String> refresh(final String refreshJwt) {
        final boolean validJwt = this.jwtUtil.validateJwt(refreshJwt, JwtType.REFRESH);
        // Send error for invalid refresh token
        if (!validJwt) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Generate a new access token
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String accessJwt = this.jwtUtil.generateJwt(authentication, JwtType.ACCESS);

        return ResponseEntity.ok().body(accessJwt);
    }

    /**
     * Builds a cookie from the refresh jwt
     *
     * @param refreshJwt refresh json web token
     * @return Cookie with refresh jwt as value
     */
    private ResponseCookie createRefreshJwtCookie(final String refreshJwt) {
        return ResponseCookie.from("_jid", refreshJwt)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMillis(this.refreshJwtExpiration))
                .path("/api/auth")
                .build();
    }

    /**
     * Builds an empty cookie so browser will delete it
     *
     * @return Cookie (empty)
     */
    private ResponseCookie resetRefreshJwtCookie() {
        return ResponseCookie.from("_jid", "")
                .maxAge(0L)
                .build();
    }
}

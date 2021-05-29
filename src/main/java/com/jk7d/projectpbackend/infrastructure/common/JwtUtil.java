package com.jk7d.projectpbackend.infrastructure.common;

import com.jk7d.projectpbackend.infrastructure.constant.JwtType;
import com.jk7d.projectpbackend.service.security.IUserDetails;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jk7d.jwt.access.secret}")
    private String accessJwtSecret;

    @Value("${jk7d.jwt.access.expiration}")
    private int accessJwtExpiration;

    @Value("${jk7d.jwt.refresh.secret}")
    private String refreshJwtSecret;

    @Value("${jk7d.jwt.refresh.expiration}")
    private int refreshJwtExpiration;


    /**
     * @param authentication
     * @param jwtType
     * @return
     */
    public String generateJwt(final Authentication authentication, final JwtType jwtType) {
        return switch (jwtType) {
            case ACCESS -> this.generateAccessJwt(authentication);
            case REFRESH -> this.generateRefreshJwt(authentication);
        };
    }

    /**
     * @param token
     * @param jwtType
     * @return
     */
    public String getUsernameFromJwt(final String token, final JwtType jwtType) {
        return switch (jwtType) {
            case ACCESS -> this.getUsernameFromAccessJwt(token);
            case REFRESH -> this.getUsernameFromRefreshJwt(token);
        };
    }

    /**
     * @param token
     * @param jwtType
     * @return
     */
    public boolean validateJwt(final String token, final JwtType jwtType) {
        return switch (jwtType) {
            case ACCESS -> this.validateAccessJwt(token);
            case REFRESH -> this.validateRefreshJwt(token);
        };
    }

    /**
     * @param authentication
     * @return
     */
    private String generateAccessJwt(final Authentication authentication) {

        final IUserDetails userPrincipal = (IUserDetails) authentication.getPrincipal();

        return Jwts.builder().setSubject((userPrincipal.getEmail())).setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date((new Date()).getTime() + this.accessJwtExpiration))
                .signWith(SignatureAlgorithm.HS512, this.accessJwtSecret)
                .compact();
    }

    /**
     * @param authentication
     * @return
     */
    private String generateRefreshJwt(final Authentication authentication) {
        final IUserDetails userPrincipal = (IUserDetails) authentication.getPrincipal();

        return Jwts.builder().setSubject((userPrincipal.getEmail())).setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date((new Date()).getTime() + this.refreshJwtExpiration))
                .signWith(SignatureAlgorithm.HS512, this.refreshJwtSecret)
                .compact();
    }

    /**
     * @param token
     * @return
     */
    private String getUsernameFromAccessJwt(final String token) {
        return Jwts.parser().setSigningKey(this.accessJwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * @param token
     * @return
     */
    private String getUsernameFromRefreshJwt(final String token) {
        return Jwts.parser().setSigningKey(this.refreshJwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * @param authToken
     * @return
     */
    private boolean validateAccessJwt(final String authToken) {
        try {
            Jwts.parser().setSigningKey(this.accessJwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (final SignatureException e) {
            LOGGER.error("Invalid (Access) JWT signature: {}", e.getMessage());
        } catch (final MalformedJwtException e) {
            LOGGER.error("Invalid (Access) JWT token: {}", e.getMessage());
        } catch (final ExpiredJwtException e) {
            LOGGER.error("(Access) JWT token is expired: {}", e.getMessage());
        } catch (final UnsupportedJwtException e) {
            LOGGER.error("(Access) JWT token is unsupported: {}", e.getMessage());
        } catch (final IllegalArgumentException e) {
            LOGGER.error("(Access) JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * @param authToken
     * @return
     */
    private boolean validateRefreshJwt(final String authToken) {
        try {
            Jwts.parser().setSigningKey(this.refreshJwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (final SignatureException e) {
            LOGGER.error("Invalid (Refresh) JWT signature: {}", e.getMessage());
        } catch (final MalformedJwtException e) {
            LOGGER.error("Invalid (Refresh) JWT token: {}", e.getMessage());
        } catch (final ExpiredJwtException e) {
            LOGGER.error("(Refresh) JWT token is expired: {}", e.getMessage());
        } catch (final UnsupportedJwtException e) {
            LOGGER.error("(Refresh) JWT token is unsupported: {}", e.getMessage());
        } catch (final IllegalArgumentException e) {
            LOGGER.error("(Refresh) JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

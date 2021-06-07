package com.jk7d.projectpbackend.store.user.confirmation;

import com.jk7d.projectpbackend.store.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity(name = "tab_confirmation_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false)
    private UUID token;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    private User user;

    public ConfirmationToken(final User user, final int expiration) {
        this.user = user;
        this.expiresAt = new Date((new Date()).getTime() + expiration);
        this.token = UUID.randomUUID();
    }

    public ConfirmationToken() {

    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public UUID getToken() {
        return this.token;
    }

    public void setToken(final UUID token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(final Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}

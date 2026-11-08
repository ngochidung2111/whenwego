package com.whenwego.wwg.feature.identity.entity;

import java.util.ArrayList;
import java.util.List;

import com.whenwego.wwg.feature.event.Event;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 24)
    private String status;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(mappedBy = "host")
    private List<Event> hostedEvents = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserRole> roles = new ArrayList<>();

    protected User() {
    }

    public User(String email, String passwordHash, String status) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public Profile getProfile() {
        return profile;
    }

    public List<UserRole> getRoles() {
        return roles;
    }
}
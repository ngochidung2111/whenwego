package com.whenwego.wwg.feature.identity;

import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_roles", indexes = {
        @Index(name = "ix_user_roles_role_user", columnList = "role_id,user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_roles_user_role", columnNames = { "user_id", "role_id" })
})
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedByUser;

    protected UserRole() {
    }

    public UserRole(User user, Role role, User assignedByUser) {
        this.user = user;
        this.role = role;
        this.assignedByUser = assignedByUser;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public User getAssignedByUser() {
        return assignedByUser;
    }
}

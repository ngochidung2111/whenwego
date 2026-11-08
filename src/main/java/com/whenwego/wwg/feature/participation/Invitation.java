package com.whenwego.wwg.feature.participation;

import java.time.Instant;

import com.whenwego.wwg.feature.event.Event;
import com.whenwego.wwg.feature.identity.entity.User;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "invitations", indexes = {
        @Index(name = "ix_invitations_event_status", columnList = "event_id,status"),
        @Index(name = "ix_invitations_created_by", columnList = "created_by_user_id")
})
public class Invitation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "invited_email", length = 320)
    private String invitedEmail;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(name = "expires_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant expiresAt;

    @Column(name = "accepted_at", columnDefinition = "DATETIME(6)")
    private Instant acceptedAt;
}
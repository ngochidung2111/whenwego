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
@Table(name = "guest_participants", indexes = {
        @Index(name = "ix_guest_participants_event", columnList = "event_id"),
        @Index(name = "ix_guest_participants_claimed_user", columnList = "claimed_by_user_id")
})
public class GuestParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "access_token_hash", nullable = false, length = 255)
    private String accessTokenHash;

    @Column(name = "token_expires_at", columnDefinition = "DATETIME(6)")
    private Instant tokenExpiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_user_id")
    private User claimedByUser;

    @Column(name = "claimed_at", columnDefinition = "DATETIME(6)")
    private Instant claimedAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(6)")
    private Instant revokedAt;
}
package com.whenwego.wwg.feature.participation;

import java.time.Instant;

import com.whenwego.wwg.feature.event.Event;
import com.whenwego.wwg.feature.identity.User;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "event_members", indexes = {
                @Index(name = "ix_event_members_event_role_status", columnList = "event_id,role,membership_status"),
                @Index(name = "ix_event_members_event_user", columnList = "event_id,user_id"),
                @Index(name = "ix_event_members_event_guest", columnList = "event_id,guest_id")
}, uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_member_event_user", columnNames = { "event_id", "user_id" }),
                @UniqueConstraint(name = "uk_event_member_event_guest", columnNames = { "event_id", "guest_id" })
}, check = {
                @CheckConstraint(
                name = "ck_event_members_participant",
                constraint = "(user_id is not null and guest_id is null) or (user_id is null and guest_id is not null)"
                )
})
public class EventMember extends BaseEntity {

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "event_id", nullable = false)
        private Event event;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "guest_id")
        private GuestParticipant guest;

        @Column(nullable = false, length = 16)
        private String role;

        @Column(name = "membership_status", nullable = false, length = 16)
        private String membershipStatus;

        @Column(name = "joined_at", nullable = false, columnDefinition = "DATETIME(6)")
        private Instant joinedAt;
}
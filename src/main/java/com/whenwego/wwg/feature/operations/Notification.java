package com.whenwego.wwg.feature.operations;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.whenwego.wwg.feature.event.Event;
import com.whenwego.wwg.feature.identity.User;
import com.whenwego.wwg.feature.participation.GuestParticipant;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "ix_notifications_user_read_created", columnList = "recipient_user_id,read_at,created_at"),
                @Index(name = "ix_notifications_guest_read_created", columnList = "recipient_guest_id,read_at,created_at")
        },
        check = {
                @CheckConstraint(
                name = "ck_notifications_recipient",
                constraint = "(recipient_user_id is not null and recipient_guest_id is null) or (recipient_user_id is null and recipient_guest_id is not null)"
                )
        }
)
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_guest_id")
    private GuestParticipant recipientGuest;

    @Column(nullable = false, length = 64)
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "JSON")
    private String payload;

    @Column(name = "read_at", columnDefinition = "DATETIME(6)")
    private Instant readAt;

    @Column(name = "delivery_status", nullable = false, length = 24)
    private String deliveryStatus;
}
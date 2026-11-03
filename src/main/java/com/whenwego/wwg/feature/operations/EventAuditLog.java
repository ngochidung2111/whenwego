package com.whenwego.wwg.feature.operations;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.whenwego.wwg.feature.event.Event;
import com.whenwego.wwg.feature.participation.EventMember;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "event_audit_logs", indexes = {
        @Index(name = "ix_event_audit_logs_event_created", columnList = "event_id,created_at"),
        @Index(name = "ix_event_audit_logs_actor", columnList = "actor_member_id")
})
public class EventAuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_member_id")
    private EventMember actorMember;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "entity_id", columnDefinition = "BINARY(16)")
    private UUID entityId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private String metadata;
}
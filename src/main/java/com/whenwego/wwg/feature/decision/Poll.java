package com.whenwego.wwg.feature.decision;

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
@Table(name = "polls", indexes = @Index(name = "ix_polls_event_status", columnList = "event_id,status"))
public class Poll extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(length = 200)
    private String title;

    @Column(name = "voting_deadline", columnDefinition = "DATETIME(6)")
    private Instant votingDeadline;

    @Column(name = "quorum_min_votes", nullable = false)
    private int quorumMinVotes;

    @Column(name = "closed_at", columnDefinition = "DATETIME(6)")
    private Instant closedAt;
}
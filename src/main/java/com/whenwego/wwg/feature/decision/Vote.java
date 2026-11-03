package com.whenwego.wwg.feature.decision;

import com.whenwego.wwg.feature.participation.EventMember;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "votes", indexes = @Index(name = "ix_votes_poll", columnList = "poll_id"), uniqueConstraints = @UniqueConstraint(name = "uk_vote_poll_member", columnNames = {
        "poll_id", "event_member_id" }))
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_option_id", nullable = false)
    private PollOption pollOption;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_member_id", nullable = false)
    private EventMember eventMember;
}
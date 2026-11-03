package com.whenwego.wwg.feature.decision;

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
@Table(name = "date_finder_runs", indexes = {
        @Index(name = "ix_date_finder_runs_event", columnList = "event_id"),
        @Index(name = "ix_date_finder_runs_requested_by", columnList = "requested_by_member_id")
})
public class DateFinderRun extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_member_id", nullable = false)
    private EventMember requestedByMember;

    @Column(name = "algorithm_version", nullable = false, length = 32)
    private String algorithmVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_snapshot", nullable = false, columnDefinition = "JSON")
    private String inputSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "JSON")
    private String result;
}
package com.whenwego.wwg.feature.event;

import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CheckConstraint;

import com.whenwego.wwg.feature.identity.User;
import com.whenwego.wwg.feature.participation.EventMember;
import com.whenwego.wwg.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "events",
    indexes = @Index(
        name = "ix_events_host_status",
        columnList = "host_user_id,status"
    ),
    check = {
        @CheckConstraint(
            name = "ck_events_valid_dates",
            constraint = """
                start_date <= end_date
                AND duration_days >= 1
                AND (
                    confirmed_start_date IS NULL
                    OR confirmed_end_date IS NULL
                    OR (
                        confirmed_start_date <= confirmed_end_date
                        AND confirmed_start_date >= start_date
                        AND confirmed_end_date <= end_date
                    )
                )
                """
        )
    }
)
public class Event extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User host;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(nullable = false, length = 64)
    private String timezone;

    @Column(name = "availability_deadline", columnDefinition = "DATETIME(6)")
    private Instant availabilityDeadline;

    @Column(name = "confirmed_start_date")
    private LocalDate confirmedStartDate;

    @Column(name = "confirmed_end_date")
    private LocalDate confirmedEndDate;

    @OneToMany(mappedBy = "event")
    private List<EventMember> members = new ArrayList<>();
}
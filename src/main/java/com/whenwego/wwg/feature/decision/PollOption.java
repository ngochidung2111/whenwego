package com.whenwego.wwg.feature.decision;

import java.time.LocalDate;

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
@Table(name = "poll_options", uniqueConstraints = {
        @UniqueConstraint(name = "uk_poll_option_dates", columnNames = { "poll_id", "start_date", "end_date" }),
        @UniqueConstraint(name = "uk_poll_option_rank", columnNames = { "poll_id", "rank_position" })
}, indexes = {
        @Index(name = "ix_poll_options_poll", columnList = "poll_id")
}, check = {
        @CheckConstraint(
        name = "ck_poll_options_valid_dates",
        constraint = "start_date <= end_date"
        )
})
public class PollOption extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "rank_position", nullable = false)
    private int rankPosition;
}
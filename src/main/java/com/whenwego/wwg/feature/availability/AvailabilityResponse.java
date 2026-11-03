package com.whenwego.wwg.feature.availability;

import java.time.LocalDate;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "availability_responses", indexes = @Index(name = "ix_availability_event_date_slot", columnList = "event_id,date,slot"), uniqueConstraints = @UniqueConstraint(name = "uk_availability_member_date_slot", columnNames = {
        "event_member_id", "date", "slot" }))
public class AvailabilityResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_member_id", nullable = false)
    private EventMember eventMember;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 16)
    private String slot = "all_day";

    @Column(nullable = false, length = 16)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String note;
}
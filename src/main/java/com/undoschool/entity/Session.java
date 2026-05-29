package com.undoschool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions", indexes = {
    @Index(name = "idx_sessions_offering", columnList = "offering_id"),
    @Index(name = "idx_sessions_time", columnList = "startTime,endTime")
})
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "offering_id", nullable = false)
    private UUID offeringId;

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime startTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime endTime;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", insertable = false, updatable = false)
    private Offering offering;

    @PrePersist
    void onCreate() { createdAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOfferingId() { return offeringId; }
    public void setOfferingId(UUID offeringId) { this.offeringId = offeringId; }
    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }
    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Offering getOffering() { return offering; }
    public void setOffering(Offering offering) { this.offering = offering; }
}

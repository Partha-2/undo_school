package com.undoschool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"parent_id", "offering_id"})
}, indexes = {
    @Index(name = "idx_bookings_parent", columnList = "parent_id")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    @Column(name = "offering_id", nullable = false)
    private UUID offeringId;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Parent parent;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", insertable = false, updatable = false)
    private Offering offering;

    @PrePersist
    void onCreate() { createdAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public UUID getOfferingId() { return offeringId; }
    public void setOfferingId(UUID offeringId) { this.offeringId = offeringId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public Parent getParent() { return parent; }
    public Offering getOffering() { return offering; }
    public void setOffering(Offering offering) { this.offering = offering; }
}

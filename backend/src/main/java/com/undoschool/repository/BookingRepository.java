package com.undoschool.repository;

import com.undoschool.entity.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByParentIdAndOfferingId(UUID parentId, UUID offeringId);

    @EntityGraph(attributePaths = {"offering.sessions", "offering.course"})
    List<Booking> findByParentId(UUID parentId);

    @EntityGraph(attributePaths = {"offering.sessions", "offering.course"})
    List<Booking> findByParentIdOrderByCreatedAtDesc(UUID parentId);
}

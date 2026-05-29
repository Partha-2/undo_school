package com.undoschool.repository;

import com.undoschool.entity.Offering;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferingRepository extends JpaRepository<Offering, UUID> {

    @EntityGraph(attributePaths = {"course", "teacher", "sessions"})
    List<Offering> findAll();

    @EntityGraph(attributePaths = {"course", "sessions"})
    List<Offering> findByTeacherIdOrderByCreatedAtDesc(UUID teacherId);

    @EntityGraph(attributePaths = {"sessions"})
    Optional<Offering> findById(UUID id);
}

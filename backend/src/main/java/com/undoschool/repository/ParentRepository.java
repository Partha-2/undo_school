package com.undoschool.repository;

import com.undoschool.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ParentRepository extends JpaRepository<Parent, UUID> {}

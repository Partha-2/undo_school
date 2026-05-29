package com.undoschool.repository;

import com.undoschool.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {}

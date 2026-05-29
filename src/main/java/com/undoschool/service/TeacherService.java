package com.undoschool.service;

import com.undoschool.dto.request.CreateTeacherRequest;
import com.undoschool.entity.Teacher;
import com.undoschool.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepo;
    private final TimezoneService tzService;

    public TeacherService(TeacherRepository teacherRepo, TimezoneService tzService) {
        this.teacherRepo = teacherRepo;
        this.tzService = tzService;
    }

    public Teacher create(CreateTeacherRequest req) {
        if (req.getTimezone() != null && !req.getTimezone().isBlank()) {
            tzService.validate(req.getTimezone());
        }
        Teacher teacher = new Teacher();
        teacher.setName(req.getName());
        teacher.setEmail(req.getEmail());
        teacher.setTimezone(req.getTimezone() != null && !req.getTimezone().isBlank() ? req.getTimezone() : "UTC");
        return teacherRepo.save(teacher);
    }
}

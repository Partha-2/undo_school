package com.undoschool.controller;

import com.undoschool.dto.request.CreateTeacherRequest;
import com.undoschool.entity.Teacher;
import com.undoschool.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Teacher create(@Valid @RequestBody CreateTeacherRequest req) {
        return teacherService.create(req);
    }
}

package com.undoschool.controller;

import com.undoschool.dto.request.CreateCourseRequest;
import com.undoschool.entity.Course;
import com.undoschool.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course create(@Valid @RequestBody CreateCourseRequest req) {
        return courseService.create(req);
    }

    @GetMapping
    public List<Course> listAll() {
        return courseService.listAll();
    }
}

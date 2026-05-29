package com.undoschool.service;

import com.undoschool.dto.request.CreateCourseRequest;
import com.undoschool.entity.Course;
import com.undoschool.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepo;

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    public Course create(CreateCourseRequest req) {
        Course course = new Course();
        course.setTitle(req.getTitle());
        course.setDescription(req.getDescription());
        return courseRepo.save(course);
    }

    public List<Course> listAll() {
        return courseRepo.findAll();
    }
}

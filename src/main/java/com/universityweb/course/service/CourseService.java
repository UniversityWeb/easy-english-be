package com.universityweb.course.service;

import com.universityweb.course.model.Course;
import com.universityweb.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public void deleteCourse(int id) {
        courseRepository.deleteById(id);
    }

    public void newCourse(Course course) {
        courseRepository.save(course);
    }

    public void updateCourse(Course course) {
        courseRepository.save(course);
    }

    public List<Course> getAllCourses(int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.getContent();
    }

    public Course getCourseById(int id) {
        return courseRepository.findById(id);
    }

    public List<Course> filterCourse(int price, String name, int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber - 1, size);
        Page<Course> coursePage = courseRepository.findByPriceGreaterThanAndTitleContaining(price, name, pageable);
        return coursePage.getContent();
    }
}

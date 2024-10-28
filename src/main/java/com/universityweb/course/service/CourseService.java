package com.universityweb.course.service;

import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService {
    Page<CourseResponse> getAllCourseOfTeacher(CourseRequest courseRequest);
    void updateCourse(CourseRequest courseRequest);
    void createCourse(CourseRequest courseRequest);
    void deleteCourse(CourseRequest courseRequest);
    CourseResponse getMainCourse(CourseRequest courseRequest);
    Course getCourseById(Long courseId);
    Page<CourseResponse> getAllCourseByTopic(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourseByLevel(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourseByListCategory(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourse(CourseRequest courseRequest);
    List<Course> getAllCourses();
    List<Course> filterCourse(int price, String name);
    List<Course> getTop10Courses();
    List<CourseResponse> getAllCourseOfStudent(CourseRequest courseRequest);
    List<CourseResponse> getAllCourseNotOfStudent(CourseRequest courseRequest);
    List<CourseResponse> getAllCourseFavoriteOfStudent(CourseRequest courseRequest);
    Course getEntityById(Long courseId);
    CourseResponse getById(Long courseId);
    public void addCourseToFavorite(CourseRequest courseRequest);

    void removeCourseFromFavorite(CourseRequest courseRequest);

    public Boolean checkCourseInFavorite(CourseRequest courseRequest);

    Page<CourseResponse> getCourseByFilter(CourseRequest courseRequest);
}

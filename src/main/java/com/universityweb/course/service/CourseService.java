package com.universityweb.course.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.request.GetRelatedCourseReq;
import com.universityweb.course.response.CourseResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CourseService extends BaseService<Course, CourseResponse, Long> {
    Page<CourseResponse> getAllCourseOfTeacher(String username, CourseRequest courseRequest);
    CourseResponse updateCourse(CourseRequest courseRequest);
    CourseResponse createCourse(CourseRequest courseRequest);
    CourseResponse getMainCourse(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourseByTopic(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourseByLevel(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourseByListCategory(CourseRequest courseRequest);
    Page<CourseResponse> getAllCourse(CourseRequest courseRequest);
    List<CourseResponse> getAllCourseOfStudent(CourseRequest courseRequest);
    List<CourseResponse> getAllCourseNotOfStudent(CourseRequest courseRequest);
    Page<CourseResponse> getCourseByFilter(CourseRequest courseRequest);
    CourseResponse mapCourseToResponse(Course course);
    CourseResponse updateStatus(User curUser, Long courseId, Course.EStatus status, String reason);
    Page<CourseResponse> getCourseForAdmin(CourseRequest req);
    CourseResponse updateCourseAdmin(Long courseId, CourseRequest req);
    List<CourseResponse> getRelatedCourses(GetRelatedCourseReq req);
    CourseResponse updateNotice(CourseRequest req);
    void incrementViewCount(Long courseId);
}

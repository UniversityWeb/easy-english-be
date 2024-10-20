package com.universityweb.course.enrollment.service;

import com.universityweb.course.enrollment.request.AddEnrollmentRequest;
import com.universityweb.course.enrollment.response.EnrollmentResponse;
import com.universityweb.course.common.entity.Course;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse addNewEnrollment(AddEnrollmentRequest addRequest);
    Long countSalesByCourseId(Long courseId);
    List<Course> findTop10CoursesBySales();
}

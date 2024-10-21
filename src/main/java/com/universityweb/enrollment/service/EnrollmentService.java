package com.universityweb.enrollment.service;

import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.response.EnrollmentResponse;
import com.universityweb.course.entity.Course;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse addNewEnrollment(AddEnrollmentRequest addRequest);
    Long countSalesByCourseId(Long courseId);
    List<Course> findTop10CoursesBySales();
}

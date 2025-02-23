package com.universityweb.userservice.enrollment.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.request.EnrolledCourseFilterReq;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EnrollmentService extends BaseService<Enrollment, EnrollmentDTO, Long> {
    EnrollmentDTO addNewEnrollment(AddEnrollmentRequest addRequest);
    Long countSalesByCourseId(Long courseId);
    List<Course> findTop10CoursesBySales();
    EnrollmentDTO isEnrolled(String username, Long courseId);

    Page<CourseResponse> getEnrolledCourses(String username, int page, int size);
    Page<CourseResponse> getEnrolledCoursesByFilter(String username, EnrolledCourseFilterReq req);
    int refreshProgress(String username, Long courseId);
}

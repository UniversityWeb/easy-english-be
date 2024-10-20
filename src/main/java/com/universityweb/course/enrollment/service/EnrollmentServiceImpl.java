package com.universityweb.course.enrollment.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.enrollment.EnrollmentMapper;
import com.universityweb.course.enrollment.EnrollmentRepos;
import com.universityweb.course.enrollment.model.Enrollment;
import com.universityweb.course.enrollment.request.AddEnrollmentRequest;
import com.universityweb.course.enrollment.response.EnrollmentResponse;
import com.universityweb.course.model.Course;
import com.universityweb.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentMapper enrollmentMapper = EnrollmentMapper.INSTANCE;

    @Autowired
    private EnrollmentRepos enrollmentRepos;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Override
    public EnrollmentResponse addNewEnrollment(AddEnrollmentRequest addRequest) {
        User user = userService.loadUserByUsername(addRequest.username());
        Course course = courseService.getCourseById(addRequest.courseId());

        Enrollment enrollment = Enrollment.builder()
                .progress(0)
                .status(addRequest.status())
                .type(addRequest.type())
                .createdAt(LocalDateTime.now())
                .lastAccessed(LocalDateTime.now())
                .user(user)
                .course(course)
                .build();
        Enrollment saved = enrollmentRepos.save(enrollment);
        return enrollmentMapper.toDTO(saved);
    }

    @Override
    public Long countSalesByCourseId(Long courseId) {
        return enrollmentRepos.countSalesByCourseId(courseId);
    }

    @Override
    public List<Course> findTop10CoursesBySales() {
        return enrollmentRepos.findTop10CoursesBySales();
    }
}

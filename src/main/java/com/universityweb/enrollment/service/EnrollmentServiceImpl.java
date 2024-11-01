package com.universityweb.enrollment.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.enrollment.mapper.EnrollmentMapper;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl
    extends BaseServiceImpl<Enrollment, EnrollmentDTO, Long, EnrollmentRepos, EnrollmentMapper>
        implements EnrollmentService {

    private final UserService userService;
    private final CourseService courseService;

    @Autowired
    public EnrollmentServiceImpl(EnrollmentRepos repository, UserService userService, CourseService courseService) {
        super(repository, EnrollmentMapper.INSTANCE);
        this.userService = userService;
        this.courseService = courseService;
    }

    @Override
    public EnrollmentDTO addNewEnrollment(AddEnrollmentRequest addRequest) {
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
        Enrollment saved = repository.save(enrollment);
        return mapper.toDTO(saved);
    }

    @Override
    public Long countSalesByCourseId(Long courseId) {
        return repository.countSalesByCourseId(courseId);
    }

    @Override
    public List<Course> findTop10CoursesBySales() {
        return repository.findTop10CoursesBySales();
    }

    @Override
    public EnrollmentDTO isEnrolled(String username, Long courseId) {
        Optional<Enrollment> enrollmentOpt = repository.findByUserUsernameAndCourseId(username, courseId);

        if (enrollmentOpt.isPresent()) {
            Enrollment enrollment = enrollmentOpt.get();
            return mapper.toDTO(enrollment);
        } else {
            return null;
        }
    }

    @Override
    public EnrollmentDTO update(Long aLong, EnrollmentDTO dto) {
        return null;
    }

    @Override
    protected void throwNotFoundException(Long aLong) {
        throw new RuntimeException("Enrollment not found");
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Enrollment entity, EnrollmentDTO dto) {
        super.setEntityRelationshipsBeforeAdd(entity, dto);

        User user = userService.loadUserByUsername(dto.username());
        Course course = courseService.getCourseById(dto.courseId());

        entity.setUser(user);
        entity.setCourse(course);
    }

    @Override
    public void softDelete(Long aLong) {
        super.softDelete(aLong);
    }
}

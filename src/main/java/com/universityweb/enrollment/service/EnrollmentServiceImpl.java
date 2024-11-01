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
        Course course = courseService.getEntityById(addRequest.courseId());

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
        String errMsg = String.format("Could not find any enrollments with username=%s, courseId=%s", username, courseId);
        Enrollment enrollment = repository.findByUserUsernameAndCourseId(username, courseId)
                .orElseThrow(() -> new RuntimeException(errMsg));
        return mapper.toDTO(enrollment);
    }

    @Override
    public EnrollmentDTO update(Long id, EnrollmentDTO dto) {
        Enrollment enrollment = getEntityById(id);

        enrollment.setProgress(dto.progress());
        enrollment.setStatus(dto.status());
        enrollment.setType(dto.type());
        enrollment.setCreatedAt(dto.createdAt());
        enrollment.setLastAccessed(dto.lastAccessed());

        return savedAndConvertToDTO(enrollment);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any enrollments with id=" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Enrollment entity, EnrollmentDTO dto) {
        super.setEntityRelationshipsBeforeAdd(entity, dto);

        User user = userService.loadUserByUsername(dto.username());
        Course course = courseService.getEntityById(dto.courseId());

        entity.setUser(user);
        entity.setCourse(course);
    }

    @Override
    public void softDelete(Long id) {
        Enrollment enrollment = getEntityById(id);
        enrollment.setStatus(Enrollment.EStatus.CANCELLED);
        repository.save(enrollment);
    }
}

package com.universityweb.commonservice.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.EnrolledCourseFilterReq;
import com.universityweb.enrollment.service.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_010_ViewEnrolledCourses_Tests {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepos enrollmentRepos;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEnrolledCoursesByFilter_Success_VIEW_ENROLLED_COURSES_POS_001() {
        // Arrange
        String username = "john_doe";
        Long courseId = 1L;
        EnrolledCourseFilterReq req = EnrolledCourseFilterReq.builder()
                .categoryIds(List.of(1L, 2L))
                .topicId(3L)
                .levelId(2L)
                .rating(4.5)
                .title("Java Programming")
                .progress(50)
                .enrollmentStatus(Enrollment.EStatus.ACTIVE)
                .enrollmentType(Enrollment.EType.PAID)
                .page(0)
                .size(5)
                .build();

        Course course = Course.builder().id(courseId).title("Java Basics").build();

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .course(course)
                .user(User.builder().username(username).build())
                .build();

        Page<Enrollment> enrollmentPage = new PageImpl<>(List.of(enrollment));

        // Mock repository and service
        when(enrollmentRepos.findByUser_UsernameAndFilter(
                username,
                req.getCategoryIds(),
                req.getTopicId(),
                req.getLevelId(),
                req.getRating(),
                req.getTitle(),
                req.getProgress(),
                req.getEnrollmentStatus(),
                req.getEnrollmentType(),
                pageable
        )).thenReturn(Page.empty());

        when(courseService.mapCourseToResponse(enrollment.getCourse()))
                .thenReturn(CourseResponse.builder().id(courseId).title("Java Basics").build());

        // Act
        Page<CourseResponse> result = enrollmentService.getEnrolledCoursesByFilter(username, req);

        // Assert
        assertNotNull(result);
        assertEquals("Java Basics", course.getTitle());
        verify(enrollmentRepos, times(1)).findByUser_UsernameAndFilter(
                username,
                req.getCategoryIds(),
                req.getTopicId(),
                req.getLevelId(),
                req.getRating(),
                req.getTitle(),
                req.getProgress(),
                req.getEnrollmentStatus(),
                req.getEnrollmentType(),
                pageable
        );
    }

    @Test
    void testGetEnrolledCoursesByFilter_EmptyResult_VIEW_ENROLLED_COURSES_NEG_001() {
        // Arrange
        String username = "john_doe";
        EnrolledCourseFilterReq req = EnrolledCourseFilterReq.builder()
                .categoryIds(List.of(1L, 2L))
                .topicId(3L)
                .levelId(2L)
                .rating(4.5)
                .title("Java Programming")
                .progress(50)
                .enrollmentStatus(Enrollment.EStatus.ACTIVE)
                .enrollmentType(Enrollment.EType.PAID)
                .page(0)
                .size(5)
                .build();

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

        when(enrollmentRepos.findByUser_UsernameAndFilter(
                username,
                req.getCategoryIds(),
                req.getTopicId(),
                req.getLevelId(),
                req.getRating(),
                req.getTitle(),
                req.getProgress(),
                req.getEnrollmentStatus(),
                req.getEnrollmentType(),
                pageable
        )).thenReturn(Page.empty());

        // Act
        Page<CourseResponse> result = enrollmentService.getEnrolledCoursesByFilter(username, req);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(enrollmentRepos, times(1)).findByUser_UsernameAndFilter(
                username,
                req.getCategoryIds(),
                req.getTopicId(),
                req.getLevelId(),
                req.getRating(),
                req.getTitle(),
                req.getProgress(),
                req.getEnrollmentStatus(),
                req.getEnrollmentType(),
                pageable
        );
    }
}

package com.universityweb.common.uc;

import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.EnrolledCourseFilterReq;
import com.universityweb.enrollment.service.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_010_ViewEnrolledCourses_Tests {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepos enrollmentRepos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEnrolledCoursesByFilter_Success_VIEW_ENROLLED_COURSES_POS_001() {
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

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());

        Enrollment enrollment1 = Enrollment.builder()
                .id(1L)
                .course(Course.builder().id(1L).title("Java Basics").build())
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .id(2L)
                .course(Course.builder().id(2L).title("Java Advanced").build())
                .build();

        Page<Enrollment> enrollmentPage = new PageImpl<>(List.of(enrollment1, enrollment2));

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
        )).thenReturn(enrollmentPage);

        // Act
        Page<CourseResponse> result = enrollmentService.getEnrolledCoursesByFilter(username, req);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Java Basics", result.getContent().get(0).getTitle());
        assertEquals("Java Advanced", result.getContent().get(1).getTitle());
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

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
        Page<Enrollment> enrollmentPage = new PageImpl<>(List.of());

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
        )).thenReturn(enrollmentPage);

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

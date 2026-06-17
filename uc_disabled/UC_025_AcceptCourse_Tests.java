package com.universityweb.common.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseServiceImpl;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_025_AcceptCourse_Tests {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepos enrollmentRepos;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CourseMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testUpdateStatus_Success_Admin() {
        // Arrange
        User adminUser = User.builder()
                .username("admin")
                .role(User.ERole.ADMIN)
                .build();

        Course course = Course.builder()
                .id(1L)
                .status(Course.EStatus.DRAFT)
                .owner(adminUser)
                .build();

        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setId(1L);
        courseResponse.setStatus(Course.EStatus.PUBLISHED);

        // Mock repository và mapper
        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(mapper.toDTO(any(Course.class))).thenReturn(courseResponse);
        when(notificationService.sendRealtimeNotification(any(AddNotificationRequest.class))).thenReturn(null);

        // Act
        CourseResponse updatedCourse = courseService.updateStatus(adminUser, 1L, Course.EStatus.PUBLISHED, "");

        // Assert
        assertNotNull(updatedCourse); // Kiểm tra không null
        assertEquals(Course.EStatus.PUBLISHED, updatedCourse.getStatus()); // Kiểm tra trạng thái cập nhật đúng
        verify(courseRepository, times(1)).save(course); // Xác minh lưu lại repository
    }

    @Test
    void testUpdateStatus_Failure_EnrolledStudents() {
        // Arrange
        User adminUser = User.builder()
                .username("admin")
                .role(User.ERole.ADMIN)
                .build();

        Course course = Course.builder()
                .id(1L)
                .status(Course.EStatus.DRAFT)
                .owner(adminUser)
                .build();

        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(enrollmentRepos.existsByCourseId(1L)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                courseService.updateStatus(adminUser, 1L, Course.EStatus.DELETED, "")
        );

        assertEquals("Cannot update the course status because it has enrolled students", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }
}
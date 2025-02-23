package com.universityweb.commonservice.uc;

import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseServiceImpl;
import com.universityweb.common.exception.CustomException;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.price.entity.Price;
import com.universityweb.price.response.PriceResponse;
import com.universityweb.review.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_006_ViewCourseDetails_Tests {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EnrollmentRepos enrollmentRepos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMainCourse_Success_AUTH_COURSE_DETAILS_POS_001() {
        // Arrange
        Long courseId = 1L;
        CourseRequest courseRequest = CourseRequest.builder()
                .id(courseId)
                .build();

        Course course = Course.builder()
                .id(courseId)
                .title("Java Programming")
                .description("Learn Java from basics to advanced.")
                .status(Course.EStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .price(Price.builder()
                        .price(new BigDecimal("199.99"))
                        .build())
                .sections(new ArrayList<>())
                .build();

        CourseResponse courseResponse = CourseResponse.builder()
                .id(courseId)
                .title("Java Programming")
                .description("Learn Java from basics to advanced.")
                .price(PriceResponse.builder()
                        .price(new BigDecimal("199.99"))
                        .build())
                .status(Course.EStatus.PUBLISHED)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMapper.toDTO(course)).thenReturn(courseResponse);

        // Act
        CourseResponse result = courseService.getMainCourse(courseRequest);

        // Assert
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("Java Programming", result.getTitle());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, times(1)).toDTO(course);
    }

    @Test
    void testGetMainCourse_NotFound_AUTH_COURSE_DETAILS_NEG_002() {
        // Arrange
        Long courseId = 1L;
        CourseRequest courseRequest = CourseRequest.builder()
                .id(courseId)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(
                CustomException.class,
                () -> courseService.getMainCourse(courseRequest)
        );

        assertEquals("Course not found", exception.getMessage());
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseMapper, never()).toDTO(any());
    }
}
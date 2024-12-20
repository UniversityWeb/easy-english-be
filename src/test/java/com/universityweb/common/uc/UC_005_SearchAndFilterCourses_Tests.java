package com.universityweb.common.uc;

import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseServiceImpl;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.review.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_005_SearchAndFilterCourses_Tests {

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
    void testGetCoursesByFilter_Success_AUTH_COURSE_FILTER_POS_001() {
        // Arrange
        CourseRequest courseRequest = CourseRequest.builder()
                .categoryIds(List.of(1L, 2L))
                .topicId(3L)
                .levelId(2L)
                .price(new BigDecimal("199.99"))
                .rating(4.5)
                .title("Java Programming")
                .pageNumber(0)
                .size(5)
                .build();

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        Course course1 = Course.builder()
                .id(1L)
                .title("Advanced Java Programming")
                .status(Course.EStatus.PUBLISHED)
                .build();

        Course course2 = Course.builder()
                .id(2L)
                .title("Basic Java Programming")
                .status(Course.EStatus.PUBLISHED)
                .build();

        Page<Course> coursePage = new PageImpl<>(List.of(course1, course2));

        CourseResponse courseResponse1 = CourseResponse.builder()
                .id(1L)
                .title("Advanced Java Programming")
                .build();

        CourseResponse courseResponse2 = CourseResponse.builder()
                .id(2L)
                .title("Basic Java Programming")
                .build();

        when(courseRepository.findCourseByFilter(
                courseRequest.getCategoryIds(),
                courseRequest.getTopicId(),
                courseRequest.getLevelId(),
                courseRequest.getPrice(),
                courseRequest.getRating(),
                courseRequest.getTitle(),
                List.of(Course.EStatus.PUBLISHED),
                pageable)).thenReturn(coursePage);

        when(courseMapper.toDTO(course1)).thenReturn(courseResponse1);
        when(courseMapper.toDTO(course2)).thenReturn(courseResponse2);

        when(reviewRepository.findByCourseId(1L)).thenReturn(List.of());
        when(reviewRepository.findByCourseId(2L)).thenReturn(List.of());

        when(enrollmentRepos.countSalesByCourseId(1L)).thenReturn(10L);
        when(enrollmentRepos.countSalesByCourseId(2L)).thenReturn(15L);

        // Act
        Page<CourseResponse> result = courseService.getCourseByFilter(courseRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Advanced Java Programming", result.getContent().get(0).getTitle());
        assertEquals("Basic Java Programming", result.getContent().get(1).getTitle());
        assertEquals(10L, result.getContent().get(0).getCountStudent());
        assertEquals(15L, result.getContent().get(1).getCountStudent());
        verify(courseRepository, times(1)).findCourseByFilter(
                courseRequest.getCategoryIds(),
                courseRequest.getTopicId(),
                courseRequest.getLevelId(),
                courseRequest.getPrice(),
                courseRequest.getRating(),
                courseRequest.getTitle(),
                List.of(Course.EStatus.PUBLISHED),
                pageable);
        verify(courseMapper, times(1)).toDTO(course1);
        verify(courseMapper, times(1)).toDTO(course2);
        verify(reviewRepository, times(1)).findByCourseId(1L);
        verify(reviewRepository, times(1)).findByCourseId(2L);
        verify(enrollmentRepos, times(1)).countSalesByCourseId(1L);
        verify(enrollmentRepos, times(1)).countSalesByCourseId(2L);
    }

    @Test
    void testGetCoursesByFilter_NoResults_AUTH_COURSE_FILTER_NEG_001() {
        // Arrange
        CourseRequest courseRequest = CourseRequest.builder()
                .categoryIds(List.of(99L))
                .topicId(88L)
                .levelId(77L)
                .price(new BigDecimal("999.99"))
                .rating(1.0)
                .title("Non-existent Course")
                .pageNumber(0)
                .size(5)
                .build();

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());

        Page<Course> coursePage = new PageImpl<>(List.of());

        when(courseRepository.findCourseByFilter(
                courseRequest.getCategoryIds(),
                courseRequest.getTopicId(),
                courseRequest.getLevelId(),
                courseRequest.getPrice(),
                courseRequest.getRating(),
                courseRequest.getTitle(),
                List.of(Course.EStatus.PUBLISHED),
                pageable)).thenReturn(coursePage);

        // Act
        Page<CourseResponse> result = courseService.getCourseByFilter(courseRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(courseRepository, times(1)).findCourseByFilter(
                courseRequest.getCategoryIds(),
                courseRequest.getTopicId(),
                courseRequest.getLevelId(),
                courseRequest.getPrice(),
                courseRequest.getRating(),
                courseRequest.getTitle(),
                List.of(Course.EStatus.PUBLISHED),
                pageable);
    }
}

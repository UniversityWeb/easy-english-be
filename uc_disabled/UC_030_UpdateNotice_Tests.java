package com.universityweb.common.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_030_UpdateNotice_Tests {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateNotice_Success() {
        // Arrange
        Course course = Course.builder()
                .id(1L)
                .notice("Old Notice")
                .build();

        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setId(1L);
        courseRequest.setNotice("Updated Notice");

        CourseResponse courseResponse = new CourseResponse();
        courseResponse.setId(1L);
        courseResponse.setNotice("Updated Notice");

        // Mock repository và mapper
        when(courseRepository.findById(1L)).thenReturn(java.util.Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(mapper.toDTO(any(Course.class))).thenReturn(courseResponse);

        // Act
        CourseResponse updatedCourse = courseService.updateNotice(courseRequest);

        // Assert
        assertNotNull(updatedCourse); // Kiểm tra không null
        assertEquals("Updated Notice", updatedCourse.getNotice()); // Kiểm tra thông báo cập nhật đúng
        verify(courseRepository, times(1)).save(course); // Xác minh lưu lại repository
    }




}

package com.universityweb.common.uc;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.statistics.CourseStatisticsRepos;
import com.universityweb.statistics.request.CourseFilterReq;
import com.universityweb.statistics.service.CourseStatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_024_ViewStatistics_Tests {

    @InjectMocks
    private CourseStatisticsServiceImpl courseStatisticsService;

    @Mock
    private CourseStatisticsRepos courseStatisticsRepos;

    @Mock
    private CourseService courseService;

    @Mock
    private MediaService mediaService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRevenueByYear_Success() {
        // Arrange
        int year = 2024;
        List<Map<String, Object>> expectedRevenue = new ArrayList<>();
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("month", 1);
        revenueData.put("revenue", new BigDecimal("10000.00"));
        expectedRevenue.add(revenueData);

        when(courseStatisticsRepos.findRevenueByYear(year)).thenReturn(expectedRevenue);

        // Act
        List<Map<String, Object>> result = courseStatisticsService.getRevenueByYear(year);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new BigDecimal("10000.00"), result.get(0).get("revenue"));
        verify(courseStatisticsRepos, times(1)).findRevenueByYear(year);
    }

    @Test
    void testGetTopCoursesByRevenueWithDetails_Success() {
        // Arrange
        String ownerUsername = "owner1";
        int month = 1;
        int year = 2024;
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        Object[] courseData = new Object[]{1L, "image.jpg", "Course Title", new BigDecimal("5000.00"), "owner1"};
        Page<Object[]> coursePage = new PageImpl<>(Collections.singletonList(courseData));

        when(courseStatisticsRepos.findTopCoursesByRevenue(ownerUsername, month, year, pageRequest)).thenReturn(coursePage);

        // Act
        Page<Map<String, Object>> result = courseStatisticsService.getTopCoursesByRevenue(ownerUsername, month, year, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Course Title", result.getContent().get(0).get("title"));
        assertEquals(new BigDecimal("5000.00"), result.getContent().get(0).get("totalRevenue"));
        verify(courseStatisticsRepos, times(1)).findTopCoursesByRevenue(ownerUsername, month, year, pageRequest);
    }
}

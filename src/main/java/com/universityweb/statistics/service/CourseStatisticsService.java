package com.universityweb.statistics.service;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.statistics.request.CourseFilterReq;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CourseStatisticsService {
    List<Map<String, Object>> getRevenueByYear(int year);
    Page<Map<String, Object>> getTopCoursesByRevenue(String ownerUsername, int month, int year, int page, int size);
    Page<CourseResponse> getTopCoursesByRevenue(CourseFilterReq req);

    List<Map<String, Object>> getCoursesForSuggestions();

    List<Map<String, Object>> getUsersForSuggestions();

    List<Map<String, Object>> getInteractionsForSuggestions();
}

package com.universityweb.statistics.service;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CourseStatisticsService {
    List<Map<String, Object>> getRevenueByYear(int year);

    Page<Map<String, Object>> getTopCoursesByRevenue(String ownerUsername, int month, int year, int page, int size);
}

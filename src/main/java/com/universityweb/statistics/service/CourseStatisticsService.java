package com.universityweb.statistics.service;

import java.util.List;
import java.util.Map;

public interface CourseStatisticsService {
    List<Map<String, Object>> getRevenueByYear(int year);
}

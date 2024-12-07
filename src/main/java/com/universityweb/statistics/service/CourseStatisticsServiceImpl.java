package com.universityweb.statistics.service;

import com.universityweb.statistics.CourseStatisticsRepos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseStatisticsServiceImpl implements CourseStatisticsService {
    private final CourseStatisticsRepos courseStatisticsRepos;

    @Override
    public List<Map<String, Object>> getRevenueByYear(int year) {
        return courseStatisticsRepos.findRevenueByYear(year);
    }
}

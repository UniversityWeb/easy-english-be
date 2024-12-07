package com.universityweb.statistics.service;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.statistics.CourseStatisticsRepos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseStatisticsServiceImpl implements CourseStatisticsService {
    private final CourseStatisticsRepos courseStatisticsRepos;
    private final MediaService mediaService;

    @Override
    public List<Map<String, Object>> getRevenueByYear(int year) {
        return courseStatisticsRepos.findRevenueByYear(year);
    }

    @Override
    public Page<Map<String, Object>> getTopCoursesByRevenue(String ownerUsername, int month, int year, int page, int size) {
        // Fetch the data from the repository
        Page<Object[]> results = courseStatisticsRepos.findTopCoursesByRevenue(ownerUsername, month, year, PageRequest.of(page, size));

        // Transform the result into the required format
        List<Map<String, Object>> courseList = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", result[0]);
            courseData.put("imagePreview", mediaService.constructFileUrl((String) result[1]));
            courseData.put("title", result[2]);
            courseData.put("totalRevenue", result[3]);
            courseData.put("ownerUsername", result[4]);
            courseList.add(courseData);
        }

        return new PageImpl<>(courseList, PageRequest.of(page, size), results.getTotalElements());
    }

}

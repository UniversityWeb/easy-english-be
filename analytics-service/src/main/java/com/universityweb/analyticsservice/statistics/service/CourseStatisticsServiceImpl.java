package com.universityweb.analyticsservice.statistics.service;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.statistics.CourseStatisticsRepos;
import com.universityweb.statistics.request.CourseFilterReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseStatisticsServiceImpl implements CourseStatisticsService {
    private final CourseStatisticsRepos courseStatisticsRepos;
    private final MediaService mediaService;
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    @Override
    public List<Map<String, Object>> getRevenueByYear(int year) {
        return courseStatisticsRepos.findRevenueByYear(year);
    }

    @Override
    public Page<CourseResponse> getTopCoursesByRevenue(CourseFilterReq req) {
        String ownerUsername = req.getOwnerUsername();
        Integer month = req.getMonth();
        Integer year = req.getYear();
        Integer page = req.getPage();
        Integer size = req.getSize();
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch the data from the repository
        Page<Object[]> results = courseStatisticsRepos.findTopCoursesByRevenue(ownerUsername, month, year, pageRequest);

        // Transform the result into the required format
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Object[] result : results) {
            Long id = (Long) result[0];
            BigDecimal revenue = (BigDecimal) result[3];

            Course course = courseService.getEntityById(id);
            CourseResponse courseResponse = courseService.mapCourseToResponse(course);
            courseResponse.setTotalRevenue(revenue);
            courseResponses.add(courseResponse);
        }

        return new PageImpl<>(courseResponses, PageRequest.of(page, size), results.getTotalElements());
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

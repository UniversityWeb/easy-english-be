package com.universityweb.statistics;


import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.review.service.ReviewService;
import com.universityweb.statistics.customenum.ETopByCriteria;
import com.universityweb.statistics.request.CourseFilterReq;
import com.universityweb.statistics.service.CourseStatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/course-statistics")
@RestController
@Tag(name = "Course statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final CourseStatisticsService courseStatisticsService;
    private final AuthService authService;
    private final ReviewService reviewService;
    private final CourseService courseService;

    @GetMapping("/top3/by-year/{year}")
    public ResponseEntity<Map<String, Object>> getRevenueByYear(
            @PathVariable int year) {
        List<Map<String, Object>> data = courseStatisticsService.getRevenueByYear(year);

        // Mocked data
//        List<Map<String, Object>> data = List.of(
//                Map.of("name", "01/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "02/" + year, "course1", 0, "course2", 30, "course3", 10),
//                Map.of("name", "03/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "04/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "05/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "06/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "07/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "08/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "09/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "10/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "11/" + year, "course1", 100, "course2", 50, "course3", 60),
//                Map.of("name", "12/" + year, "course1", 100, "course2", 50, "course3", 60)
//        );

        List<Map<String, Object>> courses = List.of(
                Map.of("key", "course1", "label", "Course 1", "color", "#82ca9d"),
                Map.of("key", "course2", "label", "Course 2", "color", "#8884d8"),
                Map.of("key", "course3", "label", "Course 3", "color", "#ffc658")
        );

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-revenue/{month}/{year}")
    public ResponseEntity<Page<Map<String, Object>>> getRevenueByMonthAndYear(
            @PathVariable int month,
            @PathVariable int year,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String username = authService.getCurrentUsername();
        Page<Map<String, Object>> topCourses = courseStatisticsService.getTopCoursesByRevenue(username, month, year, page, size);
        return ResponseEntity.ok(topCourses);
    }

    @PostMapping("/top-rating/{month}/{year}")
    public ResponseEntity<Page<CourseResponse>> getRatingByMonthAndYear(
            @RequestBody CourseFilterReq req
    ) {
        Page<CourseResponse> topCourses = reviewService.getTopCoursesByRating(req);
        return ResponseEntity.ok(topCourses);
    }

    @PostMapping("/get-top")
    public ResponseEntity<Page<CourseResponse>> getTopCourses(
            @RequestParam ETopByCriteria criteria,
            @RequestBody CourseFilterReq req
    ) {
        Page<CourseResponse> courseResponses = switch (criteria) {
            case RATING -> reviewService.getTopCoursesByRating(req);
            case REVENUE -> courseStatisticsService.getTopCoursesByRevenue(req);
            default -> Page.empty();
        };
        return ResponseEntity.ok(courseResponses);
    }
}

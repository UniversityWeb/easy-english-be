package com.universityweb.review;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import com.universityweb.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@CrossOrigin
@RequiredArgsConstructor
@Tag(name = "Review API")
public class ReviewController {

    private static final Logger LOGGER = LogManager.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.createReview(reviewRequest));
    }

    @PostMapping("/response")
    public ResponseEntity<ReviewResponse> createResponse(
            @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.createResponse(reviewRequest));
    }

    @PostMapping("/by-course")
    public ResponseEntity<List<ReviewResponse>> getReviewsByCourse(
            @RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.getReviewByCourse(reviewRequest));
    }

    @PostMapping("/by-course/star/{star}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByStar(
            @RequestBody ReviewRequest reviewRequest,
            @PathVariable int star) {
        return ResponseEntity.ok(
                reviewService.getReviewStarByCourse(reviewRequest, star)
        );
    }

    @GetMapping("/top-courses")
    public ResponseEntity<List<CourseResponse>> getTopCoursesByRating() {
        LOGGER.info("Fetching top 10 courses by rating");
        List<CourseResponse> topCourses = reviewService.getTop10CoursesByRating();
        LOGGER.info("Total courses found: {}", topCourses.size());
        return ResponseEntity.ok(topCourses);
    }
}

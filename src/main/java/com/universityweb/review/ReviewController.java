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

@CrossOrigin
@RequestMapping("/api/v1/review")
@RestController
@Tag(name = "Reviews")
@RequiredArgsConstructor
public class ReviewController {

    private static final Logger log = LogManager.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final NotificationService notificationService;

    @PostMapping("/create-review")
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest reviewRequest) {
        ReviewResponse reviewResponse = reviewService.createReview(reviewRequest);
        return ResponseEntity.ok().body(reviewResponse);
    }

    @PostMapping("/create-response")
    public ResponseEntity<ReviewResponse> createResponse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.createResponse(reviewRequest));
    }
    @PostMapping("/get-all-review-5-star-by-course")
    public ResponseEntity<List<ReviewResponse>> getReview5StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,5));
    }

    @PostMapping("/get-all-review-4-star-by-course")
    public ResponseEntity<List<ReviewResponse>> getReview4StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,4));
    }

    @PostMapping("/get-all-review-3-star-by-course")
    public ResponseEntity<List<ReviewResponse>> getReview3StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,3));
    }

    @PostMapping("/get-all-review-2-star-by-course")
    public ResponseEntity<List<ReviewResponse>> getReview2StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,2));
    }

    @PostMapping("/get-all-review-1-star-by-course")
    public ResponseEntity<List<ReviewResponse>> getReview1StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,1));
    }

    @PostMapping("/get-all-review-by-course")
    public ResponseEntity<List<ReviewResponse>> getReviewByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewByCourse(reviewRequest));
    }

    @GetMapping("/get-top-10-courses-by-rating")
    public ResponseEntity<List<CourseResponse>> getTop10CoursesByRating() {
        log.info("Fetching top 10 courses by rating");
        List<CourseResponse> topCourses = reviewService.getTop10CoursesByRating();
        log.info("Found {} top courses by rating", topCourses.size());
        return ResponseEntity.ok().body(topCourses);
    }
}

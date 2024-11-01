package com.universityweb.review;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.review.dto.ReviewDTO;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/review")
@RestController
@Tag(name = "Reviews")
public class ReviewController {

    private static final Logger log = LogManager.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create-review")
    public ResponseEntity<String> createReview(@RequestBody ReviewDTO reviewDTO) {
        reviewService.create(reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review added successfully");
    }

    @PostMapping("/get-all-review-5-star-by-course")
    public ResponseEntity<List<ReviewDTO>> getReview5StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,5));
    }

    @PostMapping("/get-all-review-4-star-by-course")
    public ResponseEntity<List<ReviewDTO>> getReview4StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,4));
    }

    @PostMapping("/get-all-review-3-star-by-course")
    public ResponseEntity<List<ReviewDTO>> getReview3StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,3));
    }

    @PostMapping("/get-all-review-2-star-by-course")
    public ResponseEntity<List<ReviewDTO>> getReview2StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,2));
    }

    @PostMapping("/get-all-review-1-star-by-course")
    public ResponseEntity<List<ReviewDTO>> getReview1StarByCourse(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.getReviewStarByCourse(reviewRequest,1));
    }

    @PostMapping("/get-all-review-by-course")
    public ResponseEntity<List<ReviewDTO>> getReviewByCourse(@RequestBody ReviewRequest reviewRequest) {
        List<ReviewDTO> reviewDTOs = reviewService.getReviewByCourse(reviewRequest);
        return ResponseEntity.ok().body(reviewDTOs);
    }

    @GetMapping("/get-top-10-courses-by-rating")
    public ResponseEntity<List<CourseResponse>> getTop10CoursesByRating() {
        log.info("Fetching top 10 courses by rating");
        List<CourseResponse> topCourses = reviewService.getTop10CoursesByRating();
        log.info("Found {} top courses by rating", topCourses.size());
        return ResponseEntity.ok().body(topCourses);
    }
}

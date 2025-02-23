package com.universityweb.engagementservice.review.service;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import com.universityweb.statistics.request.CourseFilterReq;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest reviewRequest);
    List<ReviewResponse> getReviewStarByCourse(ReviewRequest reviewRequest, int star);
    List<ReviewResponse> getReviewByCourse(ReviewRequest reviewRequest);
    List<CourseResponse> getTop10CoursesByRating();
    Page<CourseResponse> getTopCoursesByRating(CourseFilterReq req);

    ReviewResponse createResponse(ReviewRequest reviewRequest);
}

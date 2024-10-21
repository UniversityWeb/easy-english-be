package com.universityweb.review.service;

import com.universityweb.course.response.CourseResponse;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    void createReview(ReviewRequest reviewRequest);
    List<ReviewResponse> getReviewStarByCourse(ReviewRequest reviewRequest, int star);
    List<ReviewResponse> getReviewByCourse(ReviewRequest reviewRequest);
    List<CourseResponse> getTop10CoursesByRating();
}

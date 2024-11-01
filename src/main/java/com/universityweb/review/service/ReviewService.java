package com.universityweb.review.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.review.entity.Review;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.dto.ReviewDTO;

import java.util.List;

public interface ReviewService extends BaseService<Review, ReviewDTO, Long> {
    List<ReviewDTO> getReviewStarByCourse(ReviewRequest reviewRequest, int star);
    List<ReviewDTO> getReviewByCourse(ReviewRequest reviewRequest);
    List<CourseResponse> getTop10CoursesByRating();
}

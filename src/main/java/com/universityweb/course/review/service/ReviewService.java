package com.universityweb.course.review.service;

import com.universityweb.course.common.entity.Course;
import com.universityweb.course.review.entity.Review;
import com.universityweb.course.review.request.ReviewRequest;
import com.universityweb.course.review.response.ReviewResponse;
import com.universityweb.course.common.repository.CourseRepository;
import com.universityweb.course.review.ReviewRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    public void createReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        Optional<Course> courseOptional = courseRepository.findById(reviewRequest.getCourseId());

        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();

            review.setCourse(course);
            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());

            reviewRepository.save(review);
            courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found");
        }
    }

    public List<ReviewResponse> getReviewStarByCourse(ReviewRequest reviewRequest, int star) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = reviewRepository.findByCourseIdAndRating(courseId, star);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        reviews.forEach(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            BeanUtils.copyProperties(review, reviewResponse);
            reviewResponses.add(reviewResponse);
        });
        return reviewResponses;
    }

    public List<ReviewResponse> getReviewByCourse(ReviewRequest reviewRequest) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = reviewRepository.findByCourseId(courseId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        reviews.forEach(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            BeanUtils.copyProperties(review, reviewResponse);
            reviewResponses.add(reviewResponse);
        });
        return reviewResponses;
    }
}
package com.universityweb.course.service;

import com.universityweb.course.model.Course;
import com.universityweb.course.model.Review;
import com.universityweb.course.model.request.ReviewRequest;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    public void newReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        Course course = courseRepository.findById(reviewRequest.getCourseId());
        review.setCourse(course);
        review.setOwner(reviewRequest.getUser());
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        reviewRepository.save(review);
        course.updateRating();
        courseRepository.save(course);
    }
}

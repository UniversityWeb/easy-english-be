package com.universityweb.review.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.entity.Review;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final CourseMapper courseMapper;
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepos userRepos;

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Review review = new Review();
        Optional<Course> courseOptional = courseRepository.findById(reviewRequest.getCourseId());
        Optional<User> userOptional = userRepos.findById(reviewRequest.getUser());
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            review.setCourse(course);
            review.setRating(reviewRequest.getRating());
            review.setComment(reviewRequest.getComment());
            review.setUser(userOptional.get());
            Review reviewSave = reviewRepository.save(review);
            courseRepository.save(course);
            ReviewResponse reviewResponse = new ReviewResponse();
            BeanUtils.copyProperties(reviewSave, reviewResponse);
            return reviewResponse;
        } else {
            throw new RuntimeException("Course not found");
        }
    }

    @Override
    public ReviewResponse createResponse(ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewRequest.getId()).orElseThrow(() -> new RuntimeException("Review not found"));
        review.setResponse(reviewRequest.getResponse());
        Review reviewSave = reviewRepository.save(review);
        ReviewResponse reviewResponse = new ReviewResponse();
        BeanUtils.copyProperties(reviewSave, reviewResponse);
        return reviewResponse;
    }

    @Override
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

    @Override
    public List<ReviewResponse> getReviewByCourse(ReviewRequest reviewRequest) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = reviewRepository.findByCourseId(courseId);
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        reviews.forEach(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            BeanUtils.copyProperties(review, reviewResponse);
            reviewResponse.setOwner(review.getUser().getUsername());
            reviewResponses.add(reviewResponse);
        });
        return reviewResponses;
    }

    @Override
    public List<CourseResponse> getTop10CoursesByRating() {
        List<Object[]> results = reviewRepository.getTop10CoursesByRating();
        return results.stream()
                .map(result -> {
                    Course course = (Course) result[0];
                    Double avgRating = (Double) result[1];
                    Long ratingCount = (Long) result[2];

                    CourseResponse courseResponse = courseMapper.toDTO(course);
                    courseResponse.setRating(avgRating);
                    courseResponse.setRatingCount(ratingCount);

                    return courseResponse;
                })
                .toList();
    }
}

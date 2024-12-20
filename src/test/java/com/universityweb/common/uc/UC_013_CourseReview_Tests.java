package com.universityweb.common.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.service.CourseService;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import com.universityweb.review.entity.Review;
import com.universityweb.review.service.ReviewServiceImpl;
import com.universityweb.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_013_CourseReview_Tests {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReview_Success_COURSE_REVIEW_POS_001() {
        // Arrange
        Long courseId = 1L;
        String username = "john_doe";
        int rating = 5;
        String comment = "Great course!";

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .courseId(courseId)
                .rating(rating)
                .comment(comment)
                .user(username)
                .build();

        Course course = new Course();
        course.setId(courseId);

        User user = new User();
        user.setUsername(username);

        Review review = Review.builder()
                .course(course)
                .user(user)
                .rating(rating)
                .comment(comment)
                .build();

        when(courseService.getEntityById(courseId)).thenReturn(course);
        when(userService.loadUserByUsername(username)).thenReturn(user);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewResponse result = reviewService.createReview(reviewRequest);

        // Assert
        assertNotNull(result);
        assertEquals(rating, result.getRating());
        assertEquals(comment, result.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateResponse_Success_COURSE_REVIEW_POS_002() {
        // Arrange
        Long reviewId = 1L;
        String response = "Thank you for your feedback!";

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .id(reviewId)
                .response(response)
                .build();

        Review review = Review.builder()
                .id(reviewId)
                .response(null)
                .build();

        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewResponse result = reviewService.createResponse(reviewRequest);

        // Assert
        assertNotNull(result);
        assertEquals(response, result.getResponse());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}

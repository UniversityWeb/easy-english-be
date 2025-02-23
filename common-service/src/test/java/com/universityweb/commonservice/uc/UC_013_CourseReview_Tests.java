package com.universityweb.commonservice.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.service.CourseService;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.mapper.ReviewMapper;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import com.universityweb.review.entity.Review;
import com.universityweb.review.service.ReviewServiceImpl;
import com.universityweb.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.control.MappingControl;
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

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private NotificationService notificationService;

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

        User student = User.builder()
                .username(username)
                .fullName("John Doe")
                .build();

        User teacher = User.builder()
                .username("teacher")
                .fullName("Teacher")
                .build();

        Course course = new Course();
        course.setId(courseId);
        course.setOwner(teacher);

        Review review = Review.builder()
                .course(course)
                .user(student)
                .rating(rating)
                .comment(comment)
                .build();

        ReviewResponse reviewResponse = ReviewResponse.builder()
                .owner(username)
                .rating(rating)
                .comment(comment)
                .build();

        when(courseService.getEntityById(courseId)).thenReturn(course);
        when(userService.loadUserByUsername(username)).thenReturn(student);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(reviewResponse);
        when(notificationService.sendRealtimeNotification(any(AddNotificationRequest.class))).thenReturn(null);

        // Act
        ReviewResponse result = reviewService.createReview(reviewRequest);

        // Assert
        assertNotNull(review);
        assertEquals(rating, review.getRating());
        assertEquals(comment, review.getComment());
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
        assertNotNull(review);
        assertEquals(response, review.getResponse());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }
}

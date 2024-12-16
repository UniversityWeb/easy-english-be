package com.universityweb.review.service;

import com.universityweb.common.util.FrontendRoutes;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.notification.util.CourseContentNotification;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.entity.Review;
import com.universityweb.review.mapper.ReviewMapper;
import com.universityweb.review.request.ReviewRequest;
import com.universityweb.review.response.ReviewResponse;
import com.universityweb.statistics.request.CourseFilterReq;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl extends BaseServiceImpl<Review, ReviewResponse, Long, ReviewRepository, ReviewMapper> implements ReviewService {
    private final CourseMapper courseMapper;
    private final CourseService courseService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public ReviewServiceImpl(
            ReviewRepository repository,
            ReviewMapper mapper,
            CourseMapper courseMapper,
            CourseService courseService,
            UserService userService,
            NotificationService notificationService
    ) {
        super(repository, mapper);
        this.courseMapper = courseMapper;
        this.courseService = courseService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Course course = courseService.getEntityById(reviewRequest.getCourseId());
        User user = userService.loadUserByUsername(reviewRequest.getUser());
        int rating = reviewRequest.getRating();
        Review review = Review.builder()
                .course(course)
                .rating(rating)
                .comment(reviewRequest.getComment())
                .user(user)
                .build();

        ReviewResponse savedReview = savedAndConvertToDTO(review);
        notifyCourseRated(course, rating);
        return savedReview;
    }

    @Override
    public ReviewResponse createResponse(ReviewRequest reviewRequest) {
        Review review = getEntityById(reviewRequest.getId());
        review.setResponse(reviewRequest.getResponse());
        ReviewResponse reviewResponse = savedAndConvertToDTO(review);
        notifyReviewResponded(review);
        return reviewResponse;
    }

    @Override
    public List<ReviewResponse> getReviewStarByCourse(ReviewRequest reviewRequest, int star) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = repository.findByCourseIdAndRating(courseId, star);
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
        List<Review> reviews = repository.findByCourseId(courseId);
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
        List<Object[]> results = repository.getTop10CoursesByRating();
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

    @Override
    public Page<CourseResponse> getTopCoursesByRating(CourseFilterReq req) {
        String ownerUsername = req.getOwnerUsername();
        Integer month = req.getMonth();
        Integer year = req.getYear();
        Integer page = req.getPage();
        Integer size = req.getSize();
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Object[]> results = repository.getTopCoursesByRating(ownerUsername, month, year, pageRequest);

        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Object[] result : results) {
            Course course = (Course) result[0];
            Double avgRating = (Double) result[1];
            Long ratingCount = (Long) result[2];

            CourseResponse courseResponse = courseService.mapCourseToResponse(course);
            courseResponse.setRating(avgRating);
            courseResponse.setRatingCount(ratingCount);
            courseResponses.add(courseResponse);
        }

        return new PageImpl<>(courseResponses, PageRequest.of(page, size), results.getTotalElements());
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("Could not find review with id=" + id);
    }

    private void notifyCourseRated(Course course, int rating) {
        try {
            String teacherName = course.getOwner().getFullName();
            String courseTitle = course.getTitle();

            String msg = CourseContentNotification.courseRated(teacherName, courseTitle, rating);
            String url = FrontendRoutes.getCourseDetailRoute(course.getId().toString());
            AddNotificationRequest addNotiReq = AddNotificationRequest.builder()
                    .previewImage(course.getImagePreview())
                    .message(msg)
                    .url(url)
                    .username(teacherName)
                    .createdDate(LocalDateTime.now())
                    .build();

            notificationService.sendRealtimeNotification(addNotiReq);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void notifyReviewResponded(Review review) {
        try {
            Course course = review.getCourse();
            String studentName = review.getUser().getFullName();
            String teacherName = course.getOwner().getFullName();
            String courseTitle = course.getTitle();
            String response = review.getResponse();

            String msg = CourseContentNotification.reviewResponded(studentName, teacherName, courseTitle, response);
            String url = FrontendRoutes.getCourseDetailRoute(course.getId().toString());
            AddNotificationRequest addNotiReq = AddNotificationRequest.builder()
                    .previewImage(course.getImagePreview())
                    .message(msg)
                    .url(url)
                    .username(teacherName)
                    .createdDate(LocalDateTime.now())
                    .build();

            notificationService.sendRealtimeNotification(addNotiReq);
        } catch (Exception e) {
            log.error(e);
        }
    }
}

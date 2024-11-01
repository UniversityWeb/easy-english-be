package com.universityweb.review.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.dto.ReviewDTO;
import com.universityweb.review.entity.Review;
import com.universityweb.review.mapper.ReviewMapper;
import com.universityweb.review.request.ReviewRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewServiceImpl
    extends BaseServiceImpl<Review, ReviewDTO, Long, ReviewRepository, ReviewMapper>
    implements ReviewService {

    private final CourseMapper courseMapper = CourseMapper.INSTANCE;
    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository repository, CourseService courseService, UserService userService) {
        super(repository, ReviewMapper.INSTANCE);
        this.courseService = courseService;
        this.userService = userService;
    }

    @Override
    public List<ReviewDTO> getReviewStarByCourse(ReviewRequest reviewRequest, int star) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = repository.findByCourseIdAndRating(courseId, star);
        List<ReviewDTO> reviewRespons = new ArrayList<>();
        reviews.forEach(review -> {
            ReviewDTO reviewDTO = new ReviewDTO();
            BeanUtils.copyProperties(review, reviewDTO);
            reviewRespons.add(reviewDTO);
        });
        return reviewRespons;
    }

    @Override
    public List<ReviewDTO> getReviewByCourse(ReviewRequest reviewRequest) {
        Long courseId = reviewRequest.getCourseId();
        List<Review> reviews = repository.findByCourseId(courseId);
        List<ReviewDTO> reviewRespons = new ArrayList<>();
        reviews.forEach(review -> {
            ReviewDTO reviewDTO = new ReviewDTO();
            BeanUtils.copyProperties(review, reviewDTO);
            reviewDTO.setOwner(review.getUser().getUsername());
            reviewRespons.add(reviewDTO);
        });
        return reviewRespons;
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
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any review with id" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Review entity, ReviewDTO dto) {
        super.setEntityRelationshipsBeforeAdd(entity, dto);

        Course course = courseService.getCourseById(dto.getCourseId());
        User user = userService.loadUserByUsername(dto.getOwner());
        Review parentView = repository.findById(dto.getParentReviewId())
                .orElse(null);

        entity.setDeleted(false);
        entity.setCourse(course);
        entity.setUser(user);
        entity.setParentReview(parentView);
    }

    @Override
    public ReviewDTO update(Long id, ReviewDTO dto) {
        Review review = getEntityById(id);
        Review parentView = repository.findById(dto.getParentReviewId())
                .orElse(null);

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setParentReview(parentView);
        return savedAndConvertToDTO(review);
    }

    @Override
    public void softDelete(Long id) {
        Review review = getEntityById(id);
        review.setDeleted(true);
        repository.save(review);
    }
}

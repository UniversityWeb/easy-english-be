package com.universityweb.course.review;

import com.universityweb.course.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourseId(Long courseId);
    List<Review> findByCourseIdAndRating(Long courseId, double rating);
}

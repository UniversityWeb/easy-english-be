package com.universityweb.course.repository;

import com.universityweb.course.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourseId(int courseId);
}

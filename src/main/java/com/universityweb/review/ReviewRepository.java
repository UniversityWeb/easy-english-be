package com.universityweb.review;

import com.universityweb.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourseId(Long courseId);
    List<Review> findByCourseIdAndRating(Long courseId, double rating);

    @Query("SELECT c, AVG(r.rating), COUNT(r.id) " +
            "FROM Review r JOIN r.course c " +
            "GROUP BY c.id, c.countView, c.createdAt, c.description, c.descriptionPreview, c.duration, c.imagePreview, " +
            "c.status, c.level.id, c.owner.username, c.price.id, c.title, c.topic.id, c.updatedAt, c.videoPreview " +
            "ORDER BY AVG(r.rating) DESC")
    List<Object[]> getTop10CoursesByRating();

    @Query("""
        SELECT c, AVG(r.rating), COUNT(r.id)
        FROM Review r
        JOIN r.course c
        WHERE (:ownerUsername IS NULL OR c.owner.username = :ownerUsername)
        AND (:month IS NULL OR MONTH(r.createdAt) = :month)
        AND (:year IS NULL OR YEAR(r.createdAt) = :year)
        GROUP BY c.id, c.countView, c.createdAt, c.description, c.descriptionPreview, c.duration, c.imagePreview,
                 c.status, c.level.id, c.owner.username, c.price.id, c.title, c.topic.id, c.updatedAt, c.videoPreview
        ORDER BY AVG(r.rating) DESC
    """)
    Page<Object[]> getTopCoursesByRating(
            @Param("ownerUsername") String ownerUsername,
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable);
}

package com.universityweb.course.repository;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByPriceGreaterThanAndTitleContaining(int price, String title, Pageable pageable);

    Page<Course> findByOwner(User user, Pageable pageable);

    Page<Course> findByIsActiveAndOwner(Boolean isActive, User user, Pageable pageable);

    Page<Course> findByIsActiveAndCategoriesId(boolean b, Long categoryId, Pageable pageable);

    Page<Course> findByIsActiveAndTopicId(boolean b, Long topicId, Pageable pageable);

    Page<Course> findByIsActiveAndLevelId(boolean b, Long levelId, Pageable pageable);

    Page<Course> findByIsActive(boolean b, Pageable pageable);

    List<Course> findByOwnerNot(User user);

    @Query("SELECT c FROM Course c " +
            "JOIN c.categories cat " +
            "JOIN c.topic t " +
            "JOIN c.level l " +
            "JOIN c.price p " +
            "LEFT JOIN c.reviews r " +
            "WHERE (:categoryId IS NULL OR cat.id IN :categoryId) " +
            "AND (:topicId IS NULL OR t.id = :topicId) " +
            "AND (:title IS NULL OR c.title = :title) " +
            "AND (:levelId IS NULL OR l.id = :levelId) " +
            "AND (:price IS NULL OR p.price > :price) " +  // Kiểm tra nếu price không null
            "GROUP BY c.id " +
            "HAVING (COUNT(r) = 0 OR AVG(r.rating) > :rating OR :rating IS NULL)")  // Điều kiện rating
    Page<Course> findCourseByFilter(
            @Param("categoryId") List<Long> categoryId,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            Pageable pageable);

}

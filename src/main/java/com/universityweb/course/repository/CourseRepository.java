package com.universityweb.course.repository;

import com.universityweb.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByStatusAndCategoryIdsContains(Course.EStatus status, Long categoryId, Pageable pageable);

    Page<Course> findByStatusAndTopicId(Course.EStatus status, Long topicId, Pageable pageable);

    Page<Course> findByStatusAndLevelId(Course.EStatus status, Long levelId, Pageable pageable);

    Page<Course> findByStatus(Course.EStatus status, Pageable pageable);

    @Query("""
        SELECT c FROM Course c
        WHERE c.ownerUsername = :ownerUsername
        AND (:categoryId IS NULL OR EXISTS (SELECT cid FROM c.categoryIds cid WHERE cid IN :categoryId))
        AND (:topicId IS NULL OR c.topicId = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR c.levelId = :levelId) 
        AND (:price IS NULL OR EXISTS (
            SELECT p.id FROM Price p WHERE p.id = c.priceId
            AND ((p.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN p.startDate AND p.endDate AND p.salePrice <= :price)
            OR p.price <= :price)
        ))
        AND (:rating IS NULL OR (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.courseId = c.id) >= :rating)
        AND (:status IS NULL OR c.status = :status)
        AND c.status <> 'DELETED'
        ORDER BY c.createdAt DESC
    """)
    Page<Course> findCourseForTeacher(
            @Param("ownerUsername") String ownerUsername,
            @Param("categoryId") List<Long> categoryId,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            @Param("status") Course.EStatus status,
            Pageable pageable);

    @Query("""
        SELECT c FROM Course c
        WHERE (:categoryId IS NULL OR EXISTS (SELECT cid FROM c.categoryIds cid WHERE cid IN :categoryId))
        AND (:topicId IS NULL OR c.topicId = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR c.levelId = :levelId) 
        AND (:price IS NULL OR EXISTS (
            SELECT p.id FROM Price p WHERE p.id = c.priceId
            AND ((p.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN p.startDate AND p.endDate AND p.salePrice <= :price)
            OR p.price <= :price)
        ))
        AND (:rating IS NULL OR (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.courseId = c.id) >= :rating)
        AND (:statuses IS NULL OR c.status IN :statuses)
        ORDER BY c.createdAt DESC
    """)
    Page<Course> findCourseByFilter(
            @Param("categoryId") List<Long> categoryId,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            @Param("statuses") List<Course.EStatus> statuses,
            Pageable pageable);

    @Query("""
        SELECT c FROM Course c
        WHERE (:topicId IS NULL OR c.topicId = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR c.levelId = :levelId) 
        AND (:categoryId IS NULL OR EXISTS (SELECT cid FROM c.categoryIds cid WHERE cid IN :categoryId))
        AND (:price IS NULL OR EXISTS (
            SELECT p.id FROM Price p WHERE p.id = c.priceId
            AND ((p.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN p.startDate AND p.endDate AND p.salePrice <= :price)
            OR p.price <= :price)
        ))
        AND (:rating IS NULL OR (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.courseId = c.id) >= :rating)
        AND (:ownerUsername IS NULL OR LOWER(c.ownerUsername) LIKE LOWER(CONCAT('%', CAST(:ownerUsername AS text), '%'))) 
        AND (:status IS NULL OR c.status = :status) 
        ORDER BY c.createdAt DESC
    """)
    Page<Course> findCourseForAdmin(
            @Param("categoryId") List<Long> categoryId,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            @Param("ownerUsername") String ownerUsername,
            @Param("status") Course.EStatus status,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Course c SET c.countView = c.countView + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);
}

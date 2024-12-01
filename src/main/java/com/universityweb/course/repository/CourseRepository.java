package com.universityweb.course.repository;

import com.universityweb.common.auth.entity.User;
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
    Page<Course> findByPriceGreaterThanAndTitleContaining(int price, String title, Pageable pageable);

    Page<Course> findByOwner(User user, Pageable pageable);

    Page<Course> findByStatusInAndOwner(List<Course.EStatus> statuses, User owner, Pageable pageable);

    Page<Course> findByStatusNotInAndOwner(List<Course.EStatus> excludedStatuses, User owner, Pageable pageable);

    Page<Course> findByStatusAndCategoriesId(Course.EStatus status, Long categoryId, Pageable pageable);

    Page<Course> findByStatusAndTopicId(Course.EStatus status, Long topicId, Pageable pageable);

    Page<Course> findByStatusAndLevelId(Course.EStatus status, Long levelId, Pageable pageable);

    Page<Course> findByStatus(Course.EStatus status, Pageable pageable);

    @Query("""
        SELECT c FROM Course c
        JOIN c.categories cat 
        JOIN c.topic t 
        JOIN c.level l 
        JOIN c.price p 
        LEFT JOIN c.reviews r 
        WHERE c.owner.username = :ownerUsername
        AND (:categoryId IS NULL OR cat.id IN :categoryId) 
        AND (:topicId IS NULL OR t.id = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR l.id = :levelId) 
        AND (:price IS NULL OR (
            (c.price.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN c.price.startDate AND c.price.endDate 
            AND c.price.salePrice <= :price) 
            OR (c.price.price <= :price))
        ) 
        AND (:status IS NULL OR c.status = :status)
        AND c.status <> 'DELETED'
        GROUP BY c.id 
        HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
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
        JOIN c.categories cat 
        JOIN c.topic t 
        JOIN c.level l 
        JOIN c.price p 
        LEFT JOIN c.reviews r 
        WHERE (:categoryId IS NULL OR cat.id IN :categoryId) 
        AND (:topicId IS NULL OR t.id = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR l.id = :levelId) 
        AND (:price IS NULL OR (
            (c.price.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN c.price.startDate AND c.price.endDate 
            AND c.price.salePrice <= :price) 
            OR (c.price.price <= :price))
        ) 
        AND (:statuses IS NULL OR c.status IN :statuses)
        GROUP BY c.id 
        HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
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
        JOIN c.categories cat 
        JOIN c.topic t 
        JOIN c.level l 
        JOIN c.price p 
        LEFT JOIN c.reviews r 
        WHERE (:categoryId IS NULL OR cat.id IN :categoryId) 
        AND (:topicId IS NULL OR t.id = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:levelId IS NULL OR l.id = :levelId) 
        AND (:price IS NULL OR (
            (c.price.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN c.price.startDate AND c.price.endDate 
            AND c.price.salePrice <= :price) 
            OR (c.price.price <= :price))
        ) 
        AND (:ownerUsername IS NULL OR LOWER(c.owner.username) LIKE LOWER(CONCAT('%', CAST(:ownerUsername AS text), '%'))) 
        AND (:status IS NULL OR c.status = :status) 
        GROUP BY c.id 
        HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
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

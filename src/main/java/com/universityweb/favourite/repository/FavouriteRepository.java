package com.universityweb.favourite.repository;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.favourite.entity.Favourite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long>{
    Favourite findByUserAndCourse(User user, Course course);
    Page<Favourite> findByUser_UsernameAndIsDeletedFalse(String username, Pageable pageable);

    @Query("SELECT f FROM Favourite f " +
            "JOIN f.course c " +
            "WHERE f.user.username = :username " +
            "AND f.isDeleted = false " +
            "AND c.isPublish = true " +
            "AND c.isActive = true " +
            "AND (:categoryIds IS NULL OR c.categories IS EMPTY OR c.categories IN :categoryIds) " +
            "AND (:topicId IS NULL OR c.topic.id = :topicId) " +
            "AND (:levelId IS NULL OR c.level.id = :levelId) " +
            "AND (:price IS NULL OR c.price.price <= :price) " +
            "AND (:rating IS NULL OR (SELECT AVG(r.rating) FROM Review r WHERE r.course.id = c.id) >= :rating) " +
            "AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<Favourite> findByUserAndFilter(
            @Param("username") String username,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            Pageable pageable
    );
}

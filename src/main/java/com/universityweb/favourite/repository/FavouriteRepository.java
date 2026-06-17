package com.universityweb.favourite.repository;

import com.universityweb.favourite.entity.Favourite;
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
public interface FavouriteRepository extends JpaRepository<Favourite, Long>{
        Optional<Favourite> findByUsernameAndCourseIdAndIsDeletedFalse(String username, Long courseId);
        Page<Favourite> findByUsernameAndIsDeletedFalse(String username, Pageable pageable);

    @Query("SELECT f FROM Favourite f " +
            "JOIN Course c ON c.id = f.courseId " +
            "WHERE f.username = :username " +
            "AND f.isDeleted = false " +
            "AND (:categoryId IS NULL OR EXISTS (SELECT cid FROM c.categoryIds cid WHERE cid IN :categoryId)) " +
            "AND (:topicId IS NULL OR c.topicId = :topicId) " +
            "AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) " +
            "AND (:levelId IS NULL OR c.levelId = :levelId) " +
            "AND (:price IS NULL OR EXISTS (" +
            "  SELECT p.id FROM Price p WHERE p.id = c.priceId " +
            "  AND ((p.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN p.startDate AND p.endDate " +
            "  AND p.salePrice <= :price) OR (p.price <= :price))" +
            ")) " +
            "AND (:rating IS NULL OR (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.courseId = c.id) >= :rating) " +
            "ORDER BY f.createdAt DESC")
    Page<Favourite> findByUserAndFilter(
            @Param("username") String username,
            @Param("categoryId") List<Long> categoryId,
            @Param("topicId") Long topicId,
            @Param("levelId") Long levelId,
            @Param("price") BigDecimal price,
            @Param("rating") Double rating,
            @Param("title") String title,
            Pageable pageable);
}

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
    Optional<Favourite> findByUser_UsernameAndCourse_IdAndIsDeletedFalse(String username, Long courseId);
    Page<Favourite> findByUser_UsernameAndIsDeletedFalse(String username, Pageable pageable);

    @Query("SELECT f FROM Favourite f " +
            "JOIN f.course c " +
            "JOIN c.categories cat " +
            "JOIN c.topic t " +
            "JOIN c.level l " +
            "JOIN c.price p " +
            "LEFT JOIN c.reviews r " +
            "WHERE f.user.username = :username " +
            "AND f.isDeleted = false " +
            "AND (:categoryId IS NULL OR cat.id IN :categoryId) " +
            "AND (:topicId IS NULL OR t.id = :topicId) " +
            "AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) " +
            "AND (:levelId IS NULL OR l.id = :levelId) " +
            "AND (:price IS NULL OR (" +
            "  (c.price.salePrice IS NOT NULL AND CURRENT_DATE BETWEEN c.price.startDate AND c.price.endDate " +
            "  AND c.price.salePrice <= :price) " +
            "  OR (c.price.price <= :price))" +
            ") " +
            "GROUP BY f.id " +
            "HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)")
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

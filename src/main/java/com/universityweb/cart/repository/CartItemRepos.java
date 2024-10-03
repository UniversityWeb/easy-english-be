package com.universityweb.cart.repository;

import com.universityweb.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepos extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.status = :status")
    List<CartItem> findByUserUsernameAndStatus(
            @Param("status") CartItem.EStatus status);

    @Query("SELECT ci FROM CartItem ci WHERE ci.course.id = :courseId")
    Optional<CartItem> findByUsernameAndCourseId(
            @Param("courseId") Long courseId);
}

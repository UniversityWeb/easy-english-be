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
    @Query("SELECT c FROM CartItem c WHERE c.id = :cartId AND (c.status = 'ACTIVE' OR c.status = 'OUT_OF_STOCK')")
    List<CartItem> findItemsByCartIdToDisplay(@Param("cartId") Long cartId);

    @Query("SELECT c FROM CartItem c WHERE c.id = :cartId AND c.status = :status")
    List<CartItem> findByCartIdAndStatus(
            @Param("cartId") Long cartId,
            @Param("status") CartItem.EStatus status);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.course.id = :courseId")
    Optional<CartItem> findByCartIdAndCourseId(
            @Param("cartId") Long cartId,
            @Param("courseId") Long courseId);
}

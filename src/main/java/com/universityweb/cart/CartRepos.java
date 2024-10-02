package com.universityweb.cart;

import com.universityweb.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepos extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.user.username = :username AND c.status = :status")
    List<CartItem> findByUserUsernameAndStatus(
            @Param("username") String username,
            @Param("status") CartItem.EStatus status);

    @Query("SELECT ci FROM CartItem ci WHERE ci.user.username = :username AND ci.course.id = :courseId")
    Optional<CartItem> findByUsernameAndCourseId(
            @Param("username") String username,
            @Param("courseId") Long courseId);
}

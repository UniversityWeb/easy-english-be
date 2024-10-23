package com.universityweb.order.repository;

import com.universityweb.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepos extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.user.username = :username AND oi.course.id = :courseId")
    List<OrderItem> findByUsernameAndCourseId(
            @Param("username") String username,
            @Param("courseId") Long courseId);
}

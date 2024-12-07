package com.universityweb.order.repository;

import com.universityweb.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepos extends JpaRepository<Order, Long> {
    Page<Order> findByUserUsername(String username, Pageable pageable);

    Page<Order> findByUserUsernameAndStatus(String username, Order.EStatus status, Pageable pageable);
    List<Order> findByUserUsernameAndStatus(String username, Order.EStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt < :fiveMinutesAgo AND o.status <> :status")
    List<Order> findAllByCreatedAtBeforeAndStatusNot(LocalDateTime fiveMinutesAgo, Order.EStatus status);

    @Query("""
        SELECT SUM(o.totalAmount) 
        FROM Order o 
        WHERE o.user.username = :username 
        AND (:status IS NULL OR o.status = :status)
    """)
    BigDecimal getTotalAmountByUsernameAndStatus(
            @Param("username") String username,
            @Param("status") Order.EStatus status
    );

    List<Order> findByUser_Username(String username);

    Page<Order> findByStatus(Order.EStatus status, Pageable pageable);
}

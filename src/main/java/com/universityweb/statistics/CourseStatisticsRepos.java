package com.universityweb.statistics;

import com.universityweb.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CourseStatisticsRepos extends CrudRepository<Order, Long> {
    @Query("""
        SELECT new map(
            CONCAT(MONTH(o.createdAt), '/', YEAR(o.createdAt)) AS name,
            c.title AS course,
            SUM(o.totalAmount) AS revenue
        )
        FROM Order o
        JOIN o.items i
        JOIN i.course c
        WHERE YEAR(o.createdAt) = :year
        GROUP BY c.title, MONTH(o.createdAt), YEAR(o.createdAt)
        ORDER BY MONTH(o.createdAt)
    """)
    List<Map<String, Object>> findRevenueByYear(int year);

    @Query("""
        SELECT c.id, c.imagePreview, c.title, SUM(oi.price) AS totalRevenue, c.owner.username AS ownerUsername
        FROM OrderItem oi
        JOIN oi.course c
        JOIN oi.order o 
        JOIN o.payment p
        WHERE p.status = 'SUCCESS' 
        AND oi.price != 0
        AND (:month IS NULL OR MONTH(o.createdAt) = :month)
        AND (:year IS NULL OR YEAR(o.createdAt) = :year)
        AND (:ownerUsername IS NULL OR c.owner.username = :ownerUsername)
        GROUP BY c.id, c.title 
        ORDER BY totalRevenue DESC
    """)
    Page<Object[]> findTopCoursesByRevenue(
            @Param("ownerUsername") String ownerUsername,
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable);

    @Query("SELECT u.username FROM User u WHERE  u.role = 'TEACHER'")
    List<String> findAllTeacherUsernames();
}

package com.universityweb.statistics;

import com.universityweb.order.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
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
}

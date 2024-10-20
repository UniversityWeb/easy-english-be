package com.universityweb.course.price;

import com.universityweb.course.common.entity.Course;
import com.universityweb.course.price.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findByCourse(Course course);
}

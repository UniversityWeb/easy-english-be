package com.universityweb.price;

import com.universityweb.price.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, Long> {
    Optional<Price> findByCourseId(Long courseId);
}

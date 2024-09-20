package com.universityweb.course.repository;

import com.universityweb.course.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findById(int id);

    Page<Course> findByPriceGreaterThanAndTitleContaining(int price, String title, Pageable pageable);
}

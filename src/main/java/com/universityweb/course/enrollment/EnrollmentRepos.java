package com.universityweb.course.enrollment;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.common.entity.Course;
import com.universityweb.course.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepos extends JpaRepository<Enrollment, Long> {
    @Query("SELECT COUNT(e) FROM Enrollment e " +
            "WHERE e.type = 'PAID' AND e.course.id = :courseId")
    Long countSalesByCourseId(Long courseId);

    @Query("SELECT e.course FROM Enrollment e WHERE e.type = 'PAID' " +
            "GROUP BY e.course ORDER BY COUNT(e) DESC")
    List<Course> findTop10CoursesBySales();

    List<Enrollment> findByUser(User user);
}

package com.universityweb.enrollment;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepos extends JpaRepository<Enrollment, Long> {
    @Query("SELECT COUNT(e) FROM Enrollment e " +
            "WHERE e.type = 'PAID' AND e.course.id = :courseId")
    Long countSalesByCourseId(Long courseId);

    @Query("SELECT e.course FROM Enrollment e WHERE e.type = 'PAID' " +
            "GROUP BY e.course ORDER BY COUNT(e) DESC")
    List<Course> findTop10CoursesBySales();

    List<Enrollment> findByUser(User user);

    @Query("SELECT e FROM Enrollment e WHERE e.user.username = :username " +
            "AND e.course.id = :courseId AND e.status <> 'CANCELLED'")
    Optional<Enrollment> findByUserUsernameAndCourseId(String username, Long courseId);
}

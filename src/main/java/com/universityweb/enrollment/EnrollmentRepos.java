package com.universityweb.enrollment;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<Enrollment> findByUser_UsernameAndStatusNot(String username, Enrollment.EStatus eStatus, Pageable pageable);

    @Query("""
        SELECT e FROM Enrollment e 
        JOIN e.course c 
        JOIN c.categories cat 
        JOIN c.topic t 
        JOIN c.level l 
        LEFT JOIN c.reviews r 
        WHERE e.user.username = :username 
        AND e.status <> 'CANCELLED' 
        AND (:categoryIds IS NULL OR cat.id IN :categoryIds) 
        AND (:levelId IS NULL OR l.id = :levelId) 
        AND (:topicId IS NULL OR t.id = :topicId) 
        AND (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS text), '%'))) 
        AND (:progress IS NULL OR :progress = 0 OR e.progress >= :progress) 
        AND (:enrollmentStatus IS NULL OR e.status = :enrollmentStatus) 
        AND (:enrollmentType IS NULL OR e.type = :enrollmentType) 
        GROUP BY e.id, e.createdAt 
        HAVING (:rating IS NULL OR COALESCE(AVG(r.rating), 0) >= :rating)
        ORDER BY e.createdAt DESC
    """)
    Page<Enrollment> findByUser_UsernameAndFilter(
            @Param("username") String username,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("levelId") Long levelId,
            @Param("topicId") Long topicId,
            @Param("rating") Double rating,
            @Param("title") String title,
            @Param("progress") int progress,
            @Param("enrollmentStatus") Enrollment.EStatus enrollmentStatus,
            @Param("enrollmentType") Enrollment.EType enrollmentType,
            Pageable pageable);

    Optional<Enrollment> findByUser_UsernameAndCourse_Id(String username, Long courseId);
    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    boolean existsByCourseId(Long courseId);

    @Query(value = """
        SELECT
            u.username,
            u.full_name,
            u.email,
            e.created_at,
            SUM(CASE WHEN lt.is_completed = true THEN 1 ELSE 0 END) AS passed_lessons,
            COUNT(DISTINCT l.id) AS total_lessons,
            SUM(CASE WHEN tr.status = 'DONE' THEN 1 ELSE 0 END) AS passed_quizzes,
            COUNT(DISTINCT t.id) AS total_quizzes,
            e.progress
        FROM enrollments e
        JOIN users u ON e.username = u.username
        JOIN courses c ON e.course_id = c.id
        LEFT JOIN sections s ON s.course_id = c.id
        LEFT JOIN lessons l ON l.section_id = s.id
        LEFT JOIN tests t ON t.course_section_id = s.id
        LEFT JOIN lesson_trackers lt ON lt.lesson_id = l.id AND lt.username = u.username
        LEFT JOIN test_results tr ON tr.test_id = t.id AND tr.username = u.username
        WHERE c.id = :courseId
            AND (
              :studentUsername IS NULL OR
              LOWER(u.username) LIKE LOWER(CONCAT('%', :studentUsername, '%')) OR
              LOWER(u.full_name) LIKE LOWER(CONCAT('%', :studentUsername, '%'))
            )
        GROUP BY u.username, u.full_name, u.email, e.created_at, e.progress
    """, nativeQuery = true
    )
    Page<Object[]> getStudentStatistics(
            @Param("courseId") Long courseId,
            @Param("studentUsername") String studentUsername,
            Pageable pageable);

    List<Enrollment> findAllByCourseId(Long courseId);

    @Query("""
        SELECT e FROM Enrollment e
        WHERE (:courseId IS NULL OR e.course.id = :courseId)
            AND (:username IS NULL OR :username = '' OR LOWER(e.user.username) LIKE LOWER(CONCAT('%', :username, '%')))
        ORDER BY e.createdAt DESC
    """)
    Page<Enrollment> findByFilters(
            @Param("courseId") Long courseId,
            @Param("username") String username,
            Pageable pageable
    );
}

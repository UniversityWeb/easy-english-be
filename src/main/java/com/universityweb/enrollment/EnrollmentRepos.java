package com.universityweb.enrollment;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
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

    @Query("""
        SELECT 
            c.title AS courseTitle,
            COUNT(DISTINCT e.user) AS totalStudents,
            AVG(e.progress) AS averageProgress, 
            2.0 AS passedQuizzesPercentage,
            2 AS passedLessonsPercentage
        FROM Course c
        LEFT JOIN c.enrollments e
        LEFT JOIN c.sections s
        LEFT JOIN s.lessons l
        LEFT JOIN s.tests t
        LEFT JOIN LessonTracker lt ON lt.lesson = l AND lt.user = e.user
        LEFT JOIN TestResult tr ON tr.test = t AND tr.user = e.user
        WHERE c.owner.username = :teacherUsername
        AND (:courseTitle IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :courseTitle, '%')))
        GROUP BY c.id, c.title
    """)
    Page<Object[]> getCourseStatistics(
            @Param("teacherUsername") String teacherUsername,
            @Param("courseTitle") String courseTitle,
            Pageable pageable);

    @Query("""
        SELECT 
            new com.universityweb.enrollment.dto.StudentStatisticsDTO(
                e.user.username,
                e.user.fullName,
                e.user.email,
                e.createdAt,
                SUM(CASE WHEN lt.isCompleted = true THEN 1 ELSE 0 END),
                COUNT(DISTINCT l.id),
                SUM(CASE WHEN tr.status = 'DONE' THEN 1 ELSE 0 END),
                COUNT(DISTINCT t.id),
                e.progress
            )
        FROM Enrollment e
        LEFT JOIN e.course c
        LEFT JOIN c.sections s
        LEFT JOIN s.lessons l
        LEFT JOIN s.tests t
        LEFT JOIN LessonTracker lt ON lt.lesson = l AND lt.user = e.user
        LEFT JOIN TestResult tr ON tr.test = t AND tr.user = e.user
        WHERE c.owner.username = :teacherUsername
        AND c.id = :courseId
        AND (:studentUsername IS NULL OR 
             LOWER(e.user.username) LIKE LOWER(CONCAT('%', :studentUsername, '%')) OR
             LOWER(e.user.fullName) LIKE LOWER(CONCAT('%', :studentUsername, '%')))
        GROUP BY e.user.username, e.user.fullName, e.user.email, e.createdAt, e.progress
    """)
    Page<Object[]> getStudentStatistics(
            @Param("teacherUsername") String teacherUsername,
            @Param("courseId") Long courseId,
            @Param("studentUsername") String studentUsername,
            Pageable pageable);

    List<Enrollment> findAllByCourseId(Long courseId);
}

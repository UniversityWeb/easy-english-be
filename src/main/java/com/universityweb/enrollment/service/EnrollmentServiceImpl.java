package com.universityweb.enrollment.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.mapper.EnrollmentMapper;
import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.request.EnrolledCourseFilterReq;
import com.universityweb.lesson.LessonRepository;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lessontracker.LessonTracker;
import com.universityweb.lessontracker.LessonTrackerRepository;
import com.universityweb.section.SectionRepository;
import com.universityweb.section.entity.Section;
import com.universityweb.test.TestRepos;
import com.universityweb.test.entity.Test;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.entity.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl
    extends BaseServiceImpl<Enrollment, EnrollmentDTO, Long, EnrollmentRepos, EnrollmentMapper>
        implements EnrollmentService {

    private final UserService userService;
    private final CourseService courseService;
    private final SectionRepository sectionRepository;
    private final LessonTrackerRepository lessonTrackerRepository;
    private final TestResultRepos testResultRepository;
    private final LessonRepository lessonRepository;
    private final TestRepos testRepos;

    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentRepos repository,
            EnrollmentMapper enrollmentMapper,
            UserService userService,
            CourseService courseService,
            SectionRepository sectionRepository,
            LessonTrackerRepository lessonTrackerRepository,
            TestResultRepos testResultRepository,
            LessonRepository lessonRepository,
            TestRepos testRepos
    ) {
        super(repository, enrollmentMapper);
        this.userService = userService;
        this.courseService = courseService;
        this.sectionRepository = sectionRepository;
        this.lessonTrackerRepository = lessonTrackerRepository;
        this.testResultRepository = testResultRepository;
        this.lessonRepository = lessonRepository;
        this.testRepos = testRepos;
    }

    @Override
    public EnrollmentDTO addNewEnrollment(AddEnrollmentRequest addRequest) {
        User user = userService.loadUserByUsername(addRequest.username());
        Course course = courseService.getEntityById(addRequest.courseId());

        Optional<Enrollment> optionalEnrollment = repository.findByUserAndCourse(user, course);
        if (optionalEnrollment.isPresent()) {
            throw new CustomException("Enrollment already exists");
        }

        Enrollment enrollment = Enrollment.builder()
                .progress(0)
                .status(addRequest.status())
                .type(addRequest.type())
                .createdAt(addRequest.createdAt())
                .lastAccessed(addRequest.lastAccessed())
                .user(user)
                .course(course)
                .build();
        try {
            Enrollment saved = repository.save(enrollment);
            return mapper.toDTO(saved);
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public Long countSalesByCourseId(Long courseId) {
        return repository.countSalesByCourseId(courseId);
    }

    @Override
    public List<Course> findTop10CoursesBySales() {
        return repository.findTop10CoursesBySales();
    }

    @Override
    public EnrollmentDTO isEnrolled(String username, Long courseId) {
        Enrollment enrollment = repository.findByUserUsernameAndCourseId(username, courseId)
                .orElse(null);
        return mapper.toDTO(enrollment);
    }

    @Override
    public Page<CourseResponse> getEnrolledCourses(String username, int page, int size) {
        Pageable pageable = createPageable(page, size);
        Page<Enrollment> enrollmentsPage = repository.findByUser_UsernameAndStatusNot(username, Enrollment.EStatus.CANCELLED, pageable);
        return mapEnrollmentsToCourseResponses(username, enrollmentsPage);
    }

    @Override
    public Page<CourseResponse> getEnrolledCoursesByFilter(String username, EnrolledCourseFilterReq req) {
        Pageable pageable = createPageable(req.getPage(), req.getSize());

        List<Long> categoryIds = req.getCategoryIds();
        Long levelId = req.getLevelId();
        Long topicId = req.getTopicId();
        Double rating = req.getRating();
        String title = req.getTitle();
        int progress = req.getProgress();
        Enrollment.EStatus enrollmentStatus = req.getEnrollmentStatus();
        Enrollment.EType enrollmentType = req.getEnrollmentType();
        Page<Enrollment> filteredEnrollmentsPage = repository.findByUser_UsernameAndFilter(
                username, categoryIds, topicId, levelId, rating, title, progress, enrollmentStatus, enrollmentType, pageable
        );

        return mapEnrollmentsToCourseResponses(username, filteredEnrollmentsPage);
    }

    @Override
    public EnrollmentDTO update(Long id, EnrollmentDTO dto) {
        Enrollment enrollment = getEntityById(id);

        enrollment.setProgress(dto.progress());
        enrollment.setStatus(dto.status());
        enrollment.setType(dto.type());
        enrollment.setCreatedAt(dto.createdAt());
        enrollment.setLastAccessed(dto.lastAccessed());

        return savedAndConvertToDTO(enrollment);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("Could not find any enrollments with id=" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Enrollment entity, EnrollmentDTO dto) {
        super.setEntityRelationshipsBeforeAdd(entity, dto);

        User user = userService.loadUserByUsername(dto.username());
        Course course = courseService.getEntityById(dto.courseId());

        entity.setUser(user);
        entity.setCourse(course);
    }

    @Override
    public void softDelete(Long id) {
        Enrollment enrollment = getEntityById(id);
        enrollment.setStatus(Enrollment.EStatus.CANCELLED);
        repository.save(enrollment);
    }

    @Override
    public int refreshProgress(String username, Long courseId) {
        Enrollment enrollment = repository.findByUser_UsernameAndCourse_Id(username, courseId)
                .orElseThrow(() -> new CustomException("Could not find any enrollments with username=" + username + ", courseId=" + courseId));

        int progress = calculateProgress(username, courseId);
        enrollment.setProgress(progress);
        Enrollment saved = repository.save(enrollment);
        return saved.getProgress();
    }

    private int calculateProgress(String username, Long courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        int totalLessons = 0;
        int totalTests = 0;

        for (Section section : sections) {
            Long sectionId = section.getId();
            List<Lesson> lessons = lessonRepository.findBySectionId(sectionId);
            List<Test> tests = testRepos.findBySectionId(sectionId);
            totalLessons += lessons.size();
            totalTests += tests.size();
        }

        int totalItems = totalLessons + totalTests;
        if (totalItems == 0) {
            return 0;
        }

        List<LessonTracker> completedLessons = lessonTrackerRepository
                .findByUserUsernameAndLessonSectionCourseIdAndIsCompletedTrue(username, courseId);
        List<TestResult> completedTests = testResultRepository
                .findByUserUsernameAndTestSectionCourseIdAndStatus(username, courseId, TestResult.EStatus.DONE);
        int completedItems = completedLessons.size() + completedTests.size();
        double progress = ((double) completedItems / totalItems) * 100;
        return (int) Math.round(progress);
    }

    private Pageable createPageable(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        return PageRequest.of(page, size, sort);
    }

    private Page<CourseResponse> mapEnrollmentsToCourseResponses(String username, Page<Enrollment> enrollmentsPage) {
        return enrollmentsPage.map(enrollment -> {
            CourseResponse courseResponse = courseService.mapCourseToResponse(enrollment.getCourse());
            int newProgress = refreshProgress(username, courseResponse.getId());
            courseResponse.setProgress(newProgress);
            return courseResponse;
        });
    }
}

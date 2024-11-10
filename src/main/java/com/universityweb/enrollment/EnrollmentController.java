package com.universityweb.enrollment;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.EnrolledCourseFilterReq;
import com.universityweb.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/enrollments")
@RestController
@Tag(name = "Enrollments")
public class EnrollmentController extends BaseController<Enrollment, EnrollmentDTO, Long, EnrollmentService> {

    private final Logger log = LogManager.getLogger(EnrollmentController.class);

    private final AuthService authService;
    private final MediaService mediaService;

    @Autowired
    public EnrollmentController(
            EnrollmentService service,
            AuthService authService,
            MediaService mediaService
    ) {
        super(service);
        this.authService = authService;
        this.mediaService = mediaService;
    }

    @GetMapping("/is-enrolled/{courseId}")
    public ResponseEntity<EnrollmentDTO> isEnrolled(
            @PathVariable Long courseId
    ) {
        log.info("Entering isEnrolled method with courseId: {}", courseId);
        String username = authService.getCurrentUsername();
        log.debug("Current username: {}", username);

        EnrollmentDTO enrollmentDTO = service.isEnrolled(username, courseId);

        if (enrollmentDTO != null) {
            log.info("User is enrolled in course with courseId: {}", courseId);
        } else {
            log.info("User is not enrolled in course with courseId: {}", courseId);
        }

        return ResponseEntity.ok(enrollmentDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getEnrolledCourses(
            @RequestParam int page,
            @RequestParam int size
    ) {
        log.info("Entering getEnrolledCourses method with page: {}, size: {}", page, size);
        String username = authService.getCurrentUsername();
        log.debug("Current username: {}", username);

        Page<CourseResponse> courseResponses = service.getEnrolledCourses(username, page, size);
        log.debug("Found {} enrolled courses for user: {}", courseResponses.getTotalElements(), username);

        Page<CourseResponse> coursesWithMediaUrls = MediaUtils.addCourseMediaUrls(mediaService, courseResponses);
        return ResponseEntity.ok(coursesWithMediaUrls);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<CourseResponse>> getEnrolledCoursesWithFilter(
            @RequestBody EnrolledCourseFilterReq req
    ) {
        log.info("Entering getEnrolledCoursesWithFilter method with filter request: {}", req);
        String username = authService.getCurrentUsername();
        log.debug("Current username: {}", username);

        Page<CourseResponse> courseResponses = service.getEnrolledCoursesByFilter(username, req);
        log.debug("Found {} enrolled courses for user: {} with the provided filter", courseResponses.getTotalElements(), username);

        Page<CourseResponse> coursesWithMediaUrls = MediaUtils.addCourseMediaUrls(mediaService, courseResponses);
        return ResponseEntity.ok(coursesWithMediaUrls);
    }
}

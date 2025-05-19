package com.universityweb.course.controller;

import com.universityweb.cart.service.CartService;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.customenum.ECourseDetailButtonStatus;
import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.request.GetRelatedCourseReq;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.enrollment.dto.EnrollmentDTO;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/course")
@RestController
@Tag(name = "Courses")
@RequiredArgsConstructor
public class CourseController {

    private static final Logger log = LogManager.getLogger(CourseController.class);

    private final CourseService courseService;
    private final MediaService mediaService;
    private final AuthService authService;
    private final CartService cartService;
    private final OrderService orderService;
    private final EnrollmentService enrollmentService;

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/get-all-course-of-teacher")
    public ResponseEntity<Page<CourseResponse>> getAllCourseOfTeacher(@RequestBody CourseRequest courseRequest) {
        String username = authService.getCurrentUsername();
        Page<CourseResponse> courseResponses = courseService.getAllCourseOfTeacher(username, courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/get-all-course-of-student")
    public ResponseEntity<List<CourseResponse>> getAllCourseOfStudent(@RequestBody CourseRequest courseRequest) {
        List<CourseResponse> courseResponses = courseService.getAllCourseOfStudent(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/get-all-course-not-of-student")
    public ResponseEntity<List<CourseResponse>> getAllCourseNotOfStudent(@RequestBody CourseRequest courseRequest) {
        List<CourseResponse> courseResponses = courseService.getAllCourseNotOfStudent(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/get-all-course")
    public ResponseEntity<Page<CourseResponse>> getAllCourse(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getAllCourse(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/update-course")
    public ResponseEntity<CourseResponse> updateCourse(
            @ModelAttribute CourseRequest courseRequest,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Long courseId = courseRequest.getId();
        Course course = courseService.getEntityById(courseId);

        if (video != null && !video.isEmpty()) {
            try {
                mediaService.deleteFile(course.getVideoPreview());
                String imagePreview = mediaService.uploadFile(video);
                courseRequest.setVideoPreview(imagePreview);
            } catch (Exception e) {
                log.error("Failed to upload video file for course request: {}", e.getMessage(), e);
            }
        }

        if (image != null && !image.isEmpty()) {
            try {
                mediaService.deleteFile(course.getImagePreview());
                String videoPreview = mediaService.uploadFile(image);
                courseRequest.setImagePreview(videoPreview);
            } catch (Exception e) {
                log.error("Failed to upload image file for course request: {}", e.getMessage(), e);
            }
        }

        CourseResponse courseResponse = courseService.updateCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseResponse);
    }

    @PostMapping("/create-course")
    public ResponseEntity<CourseResponse> createCourse(
            @ModelAttribute CourseRequest courseRequest,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        processCourseMedia(courseRequest, video, image);

        CourseResponse courseResponse = courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseResponse);
    }

    @PostMapping("/delete-course")
    public ResponseEntity<String> deleteCourse(@RequestBody CourseRequest courseRequest) {
        courseService.delete(courseRequest.getId());
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }

    @PostMapping("/get-main-course")
    public ResponseEntity<CourseResponse> getMainCourse(@RequestBody CourseRequest courseRequest) {
        CourseResponse courseResponse = courseService.getMainCourse(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponse));
    }

    @PostMapping("/get-all-course-by-list-category")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByListCategory(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getAllCourseByListCategory(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/get-all-course-by-topic")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByTopic(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getAllCourseByTopic(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/get-all-course-by-level")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByLevel(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getAllCourseByLevel(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PostMapping("/get-course-by-filter")
    public ResponseEntity<Page<CourseResponse>> filterCourses(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getCourseByFilter(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PutMapping("/update-status/{courseId}/{status}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<CourseResponse> updateStatus(
            @PathVariable Long courseId,
            @PathVariable Course.EStatus status,
            @RequestParam(required = false) String reason
    ) {
        User curUser = authService.getCurUser();
        CourseResponse courseResponse = courseService.updateStatus(curUser, courseId, status, reason);
        return ResponseEntity.ok(courseResponse);
    }

    @PostMapping("/get-related-courses")
    public ResponseEntity<List<CourseResponse>> getRelatedCourses(
            @RequestBody GetRelatedCourseReq req
    ){
        List<CourseResponse> courses = courseService.getRelatedCourses(req);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courses));
    }

    @PostMapping("/admin/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CourseResponse>> getAllCourseForAdmin(
            @RequestBody CourseRequest req
    ) {
        Page<CourseResponse> courseResponses = courseService.getCourseForAdmin(req);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

    @PutMapping("/admin/update/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> updateCourseAdmin(
            @PathVariable Long courseId,
            @RequestBody CourseRequest req
    ) {
        CourseResponse courseResponse = courseService.updateCourseAdmin(courseId, req);
        return ResponseEntity.ok(courseResponse);
    }

    @PutMapping("/update-notice")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> updateNotice(
            @RequestBody CourseRequest req
    ) {
        CourseResponse courseResponse = courseService.updateNotice(req);
        return ResponseEntity.ok(courseResponse);
    }

    @PutMapping("/count-view/{courseId}")
    public ResponseEntity<Void> countView(
            @PathVariable Long courseId
    ) {
        courseService.incrementViewCount(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-button-status/{courseId}")
    public ResponseEntity<ECourseDetailButtonStatus> getCourseDetailButtonStatus(
            @PathVariable
            Long courseId
    ) {
        String username = authService.getCurrentUsername();

        boolean isPurchasedCourse = orderService.isPurchasedCourse(username, courseId);
        EnrollmentDTO enrollmentDTO = enrollmentService.isEnrolled(username, courseId);
        if (isPurchasedCourse && enrollmentDTO != null) {
            if (enrollmentDTO.progress() == 100) {
                return ResponseEntity.ok(ECourseDetailButtonStatus.COMPLETED);
            }
            if (enrollmentDTO.progress() == 0) {
                return ResponseEntity.ok(ECourseDetailButtonStatus.START_COURSE);
            }
            return ResponseEntity.ok(ECourseDetailButtonStatus.CONTINUE_COURSE);
        }

        boolean existNotInCart = cartService.existNotInCart(username, courseId);
        if (!existNotInCart) {
            return ResponseEntity.ok(ECourseDetailButtonStatus.IN_CART);
        }

        return ResponseEntity.ok(ECourseDetailButtonStatus.ADD_TO_CART);
    }

    private void processCourseMedia(CourseRequest courseRequest, MultipartFile video, MultipartFile image) {
        if (video != null && !video.isEmpty()) {
            try {
                String videoPreview = mediaService.uploadFile(video);
                courseRequest.setVideoPreview(videoPreview);
            } catch (Exception e) {
                log.error("Failed to upload video file for course request: {}", e.getMessage(), e);
            }
        }

        if (image != null && !image.isEmpty()) {
            try {
                String imagePreview = mediaService.uploadFile(image);
                courseRequest.setImagePreview(imagePreview);
            } catch (Exception e) {
                log.error("Failed to upload image file for course request: {}", e.getMessage(), e);
            }
        }
    }
}

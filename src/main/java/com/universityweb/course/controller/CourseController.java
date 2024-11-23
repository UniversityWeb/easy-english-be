package com.universityweb.course.controller;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    private final CourseService courseService;
    private final MediaService mediaService;
    private final AuthService authService;

    @PostMapping("/get-all-course-of-teacher")
    public ResponseEntity<Page<CourseResponse>> getAllCourseOfTeacher(@RequestBody CourseRequest courseRequest) {
        Page<CourseResponse> courseResponses = courseService.getAllCourseOfTeacher(courseRequest);
        return ResponseEntity.ok(MediaUtils.addCourseMediaUrls(mediaService, courseResponses));
    }

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
    public ResponseEntity<String> updateCourse(
            @ModelAttribute CourseRequest courseRequest,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Long courseId = courseRequest.getId();
        Course course = courseService.getEntityById(courseId);
        mediaService.deleteFile(course.getVideoPreview());
        mediaService.deleteFile(course.getImagePreview());

        processCourseMedia(courseRequest, video, image);

        courseService.updateCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course updated successfully");
    }

    @PostMapping("/create-course")
    public ResponseEntity<String> createCourse(
            @ModelAttribute CourseRequest courseRequest,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        processCourseMedia(courseRequest, video, image);

        courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added successfully");
    }

    @PostMapping("/delete-course")
    public ResponseEntity<String> deleteCourse(@RequestBody CourseRequest courseRequest) {
        courseService.softDelete(courseRequest.getId());
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
            @PathVariable Course.EStatus status
    ) {
        User curUser = authService.getCurUser();
        CourseResponse courseResponse = courseService.updateStatus(curUser, courseId, status);
        return ResponseEntity.ok(courseResponse);
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

    private void processCourseMedia(CourseRequest courseRequest, MultipartFile video, MultipartFile image) {
        if (video != null && !video.isEmpty()) {
            String videoPreview = mediaService.uploadFile(video);
            courseRequest.setVideoPreview(videoPreview);
        }

        if (image != null && !image.isEmpty()) {
            String imagePreview = mediaService.uploadFile(image);
            courseRequest.setImagePreview(imagePreview);
        }
    }

//    @GetMapping("")
//    public ResponseEntity<List<Course>> getAllCourse() {
//        return ResponseEntity.ok(courseService.getAllCourses());
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<Course>> filterCourse(@RequestParam int price, @RequestParam String name) {
//        return ResponseEntity.ok(courseService.filterCourse(price, name));
//    }
//
//    @GetMapping("/{courseId}/sales-count")
//    public ResponseEntity<Long> getSalesCountForCourse(@PathVariable Long courseId) {
//        Long sales = enrollmentService.countSalesByCourseId(courseId);
//        return ResponseEntity.ok(sales);
//    }
//
//    @GetMapping("/top-10-sales")
//    public ResponseEntity<List<Course>> getTop10CoursesBySales() {
//        List<Course> courses = courseService.getTop10Courses();
//        return ResponseEntity.ok(courses);
//    }
}

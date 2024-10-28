package com.universityweb.course.controller;

import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.file.UploadFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/course")
@RestController
@Tag(name = "Courses")
public class CourseController {
    @Autowired
    private CourseService courseService;


    @Autowired
    private UploadFileService uploadFileService;
    @PostMapping("/get-all-course-of-teacher")
    public ResponseEntity<Page<CourseResponse>> getAllCourseOfTeacher(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseOfTeacher(courseRequest));
    }

    @PostMapping("/get-all-course-of-student")
    public ResponseEntity<List<CourseResponse>> getAllCourseOfStudent(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseOfStudent(courseRequest));
    }

    @PostMapping("/get-all-course-not-of-student")
    public ResponseEntity<List<CourseResponse>> getAllCourseNotOfStudent(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseNotOfStudent(courseRequest));
    }

    @PostMapping("/get-all-course-favorite-of-student")
    public ResponseEntity<List<CourseResponse>> getAllCourseFavoriteOfStudent(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseFavoriteOfStudent(courseRequest));
    }

    @PostMapping("/get-all-course")
    public ResponseEntity<Page<CourseResponse>> getAllCourse(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourse(courseRequest));
    }

    @PostMapping("/update-course")
    public ResponseEntity<String> updateCourse(@ModelAttribute CourseRequest courseRequest, @RequestParam("video") MultipartFile video,@RequestParam("image") MultipartFile image) throws IOException {
        String videoPreview = uploadFileService.uploadFile(video);
        String imagePreview = uploadFileService.uploadFile(image);
        courseRequest.setImagePreview(imagePreview);
        courseRequest.setVideoPreview(videoPreview);
        courseService.updateCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course updated successfully");
    }
    @PostMapping("/create-course")
    public ResponseEntity<String> createCourse(@ModelAttribute CourseRequest courseRequest, @RequestParam("video") MultipartFile video,@RequestParam("image") MultipartFile image) throws IOException {
        String videoPreview = uploadFileService.uploadFile(video);
        String imagePreview = uploadFileService.uploadFile(image);
        courseRequest.setImagePreview(imagePreview);
        courseRequest.setVideoPreview(videoPreview);
        courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added successfully");
    }
    @PostMapping("/delete-course")
    public ResponseEntity<String> deleteCourse(@RequestBody CourseRequest courseRequest) {
        courseService.deleteCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Course deleted successfully");
    }
    @PostMapping("/get-main-course")
    public ResponseEntity<CourseResponse> getMainCourse(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok( courseService.getMainCourse(courseRequest));
    }
    @PostMapping("/get-all-course-by-list-category")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByListCategory(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseByListCategory(courseRequest));
    }

    @PostMapping("/get-all-course-by-topic")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByTopic(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseByTopic(courseRequest));
    }

    @PostMapping("/get-all-course-by-level")
    public ResponseEntity<Page<CourseResponse>> getAllCourseByLevel(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getAllCourseByLevel(courseRequest));
    }

    @PostMapping("add-course-to-favorite")
    public ResponseEntity<String> addCourseToFavorite(@RequestBody CourseRequest courseRequest) {
        courseService.addCourseToFavorite(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added to favorite successfully");
    }

    @PostMapping("remove-course-from-favorite")
    public ResponseEntity<String> removeCourseFromFavorite(@RequestBody CourseRequest courseRequest) {
        courseService.removeCourseFromFavorite(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course removed from favorite successfully");
    }

    @PostMapping("check-course-in-favorite")
    public ResponseEntity<Boolean> checkCourseInFavorite(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.checkCourseInFavorite(courseRequest));
    }

    @PostMapping("/get-course-by-filter")
    public ResponseEntity<Page<CourseResponse>> filterCourses(@RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(courseService.getCourseByFilter(courseRequest));
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

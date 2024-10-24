package com.universityweb.lesson;

import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.file.UploadFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/lesson")
@RestController
@Tag(name = "Lessons")
public class LessonController {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private UploadFileService uploadFileService;

    @PostMapping("/create-lesson")
    public String createLesson(@ModelAttribute LessonRequest lessonRequest,@RequestParam("file") MultipartFile file) throws IOException {
        String url = uploadFileService.uploadFile(file);
        lessonRequest.setContentUrl(url);
        lessonService.createLesson(lessonRequest);
        return "Lesson added successfully";
    }
    @PostMapping("/update-lesson")
    public ResponseEntity<String> updateLesson(@ModelAttribute LessonRequest lessonRequest,@RequestParam("file") MultipartFile file) throws IOException {
        String url = uploadFileService.uploadFile(file);
        lessonRequest.setContentUrl(url);
        lessonService.updateLesson(lessonRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson updated successfully");
    }
    @PostMapping("")
    public ResponseEntity<String> deleteLesson(@RequestBody LessonRequest lessonRequest) {
        lessonService.deleteLesson(lessonRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }
    @PostMapping("get-lesson-by-id")
    public LessonResponse getLessonById(@RequestBody LessonRequest lessonRequest) {
        return lessonService.getLessonById(lessonRequest);
    }
    @PostMapping("get-all-lesson-by-section")
    public List<LessonResponse> getAllLessonBySection(@RequestBody LessonRequest lessonRequest) {
        return lessonService.getAllLessonBySection(lessonRequest);
    }

}
package com.universityweb.lesson;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.lessontracker.service.LessonTrackerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/lesson")
@RestController
@Tag(name = "Lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final MediaService mediaService;
    private final AuthService authService;
    private final LessonTrackerService lessonTrackerService;

    @PostMapping("/create-lesson")
    public ResponseEntity<LessonResponse> createLesson(
            @ModelAttribute LessonRequest lessonRequest,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        handleFileUpload(lessonRequest, file);
        LessonResponse lessonResponse = lessonService.createLesson(lessonRequest);
        return ResponseEntity.ok().body(MediaUtils.attachLessonMediaUrls(mediaService, lessonResponse));
    }

    @PostMapping("/update-lesson")
    public ResponseEntity<LessonResponse> updateLesson(
            @ModelAttribute LessonRequest lessonRequest,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        mediaService.deleteFile(lessonRequest.getContentUrl());
        handleFileUpload(lessonRequest, file);
        LessonResponse lessonResponse = lessonService.updateLesson(lessonRequest);
        return ResponseEntity.ok().body(MediaUtils.attachLessonMediaUrls(mediaService, lessonResponse));
    }

    @PostMapping("")
    public ResponseEntity<String> deleteLesson(@RequestBody LessonRequest lessonRequest) {
        lessonService.deleteLesson(lessonRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PostMapping("get-lesson-by-id")
    public LessonResponse getLessonById(@RequestBody LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.getById(lessonRequest.getId());
        return populateLessonDetails(lessonResponse);
    }

    @PostMapping("get-all-lesson-by-section")
    public List<LessonResponse> getAllLessonBySection(
            @RequestBody LessonRequest lessonRequest
    ) {
        String username = authService.getCurrentUsername();
        List<LessonResponse> lessonResponses = lessonService
                .getAllLessonBySection(username, lessonRequest);
        return lessonResponses.stream()
                .map(this::populateLessonDetails)
                .toList();
    }

    private LessonResponse populateLessonDetails(LessonResponse lessonResponse) {
        String username = authService.getCurrentUsername();
        boolean isCompleted = lessonTrackerService.isLearned(username, lessonResponse.getId());
        lessonResponse.setCompleted(isCompleted);
        return MediaUtils.attachLessonMediaUrls(mediaService, lessonResponse);
    }

    private void handleFileUpload(LessonRequest lessonRequest, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String url = mediaService.uploadFile(file);
            lessonRequest.setContentUrl(url);
        }
    }
}

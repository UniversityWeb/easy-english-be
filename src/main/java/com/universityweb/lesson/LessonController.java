package com.universityweb.lesson;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/lesson")
@RestController
@Tag(name = "Lessons")
public class LessonController {
    @Autowired
    private LessonService lessonService;
    @Autowired
    private MediaService mediaService;

    @PostMapping("/create-lesson")
    public ResponseEntity<LessonResponse> createLesson(
            @ModelAttribute LessonRequest lessonRequest,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        handleFileUpload(lessonRequest, file);
        LessonResponse lessonResponse = lessonService.createLesson(lessonRequest);
        return ResponseEntity.ok().body(constructMediaUrl(lessonResponse));
    }

    @PostMapping("/update-lesson")
    public ResponseEntity<LessonResponse> updateLesson(
            @ModelAttribute LessonRequest lessonRequest,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        mediaService.deleteFile(lessonRequest.getContentUrl());
        handleFileUpload(lessonRequest, file);
        LessonResponse lessonResponse = lessonService.updateLesson(lessonRequest);
        return ResponseEntity.ok().body(constructMediaUrl(lessonResponse));
    }

    @PostMapping("")
    public ResponseEntity<String> deleteLesson(@RequestBody LessonRequest lessonRequest) {
        lessonService.deleteLesson(lessonRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

    @PostMapping("get-lesson-by-id")
    public LessonResponse getLessonById(@RequestBody LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.getById(lessonRequest.getId());
        return constructMediaUrl(lessonResponse);
    }

    @PostMapping("get-all-lesson-by-section")
    public List<LessonResponse> getAllLessonBySection(@RequestBody LessonRequest lessonRequest) {
        List<LessonResponse> lessonResponses = lessonService.getAllLessonBySection(lessonRequest);
        return constructMediaUrl(lessonResponses);
    }

    private void handleFileUpload(LessonRequest lessonRequest, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String url = mediaService.uploadFile(file);
            lessonRequest.setContentUrl(url);
        }
    }

    private Page<LessonResponse> constructMediaUrl(Page<LessonResponse> lessonResponses) {
        return lessonResponses.map(this::setMediaUrls);
    }

    private List<LessonResponse> constructMediaUrl(List<LessonResponse> lessonResponses) {
        return lessonResponses.stream()
                .map(this::setMediaUrls)
                .toList();
    }

    private LessonResponse constructMediaUrl(LessonResponse lessonResponse) {
        return setMediaUrls(lessonResponse);
    }

    private LessonResponse setMediaUrls(LessonResponse response) {
        response.setContentUrl(mediaService.constructFileUrl(response.getContentUrl()));
        return response;
    }
}

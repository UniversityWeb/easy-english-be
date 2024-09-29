package com.universityweb.course.controller;

import com.universityweb.course.model.Lesson;
import com.universityweb.course.model.request.LessonRequest;
import com.universityweb.course.model.response.LessonResponse;
import com.universityweb.course.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/lessons")
@RestController
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("")
    public List<Lesson> getAllLesson() {
        return lessonService.getAllLessons();
    }

    @PostMapping("")
    public String newLesson(@RequestBody LessonRequest lessonRequest) {
        lessonService.newLesson(lessonRequest);
        return "Lesson added successfully";
    }

    @GetMapping("getAllLessonBySection")
    public List<LessonResponse> getAllLessonBySectionV1(@RequestParam int sectionId) {
        return lessonService.getAllLessonBySectionV1(sectionId);
    }

    @GetMapping("getLessonById")
    public LessonResponse getLessonByIdV1(@RequestParam int id) {
        return lessonService.getLessonByIdV1(id);
    }

    @PutMapping("")
    public String updateLesson(@RequestBody Lesson lesson) {
        lessonService.updateLesson(lesson);
        return "Lesson updated successfully";
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteLesson(@RequestParam int id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully");
    }

}

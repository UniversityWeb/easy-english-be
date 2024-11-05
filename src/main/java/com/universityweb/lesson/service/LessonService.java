package com.universityweb.lesson.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;

import java.util.List;

public interface LessonService extends BaseService<Lesson, LessonResponse, Long> {
    List<LessonResponse> getAllLessonBySection(LessonRequest lessonRequest);
    LessonResponse createLesson(LessonRequest lessonRequest);
    LessonResponse updateLesson(LessonRequest lessonRequest);
    void deleteLesson(LessonRequest lessonRequest);
}

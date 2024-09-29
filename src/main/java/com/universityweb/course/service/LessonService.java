package com.universityweb.course.service;


import com.universityweb.course.model.Lesson;
import com.universityweb.course.model.Section;
import com.universityweb.course.model.request.LessonRequest;
import com.universityweb.course.model.response.LessonResponse;
import com.universityweb.course.repository.LessonRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionService sectionService;

    /*public void copy(Lesson lesson, LessonResponse lessonResponse) {
        lessonResponse.setId(lesson.getId());
        lessonResponse.setTitle(lesson.getTitle());
        lessonResponse.setType(lesson.getType());
        lessonResponse.setContent(lesson.getContent());
        lessonResponse.setContentUrl(lesson.getContentUrl());
        lessonResponse.setDescription(lesson.getDescription());
        lessonResponse.setDuration(lesson.getDuration());
        lessonResponse.setIsPreview(lesson.getIsPreview());
        lessonResponse.setStartDate(lesson.getStartDate());
        lessonResponse.setCreatedAt(lesson.getCreatedAt());
        lessonResponse.setCreatedBy(lesson.getCreatedBy());
    }*/


    public void copy(Lesson lesson, LessonRequest lessonRequest) {
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setType(lessonRequest.getType());
        lesson.setContent(lessonRequest.getContent());
        lesson.setContentUrl(lessonRequest.getContentUrl());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setDuration(lessonRequest.getDuration());
        lesson.setIsPreview(lessonRequest.getIsPreview());
        lesson.setStartDate(lessonRequest.getStartDate());
        lesson.setCreatedAt(lessonRequest.getCreatedAt());
        lesson.setCreatedBy(lessonRequest.getCreatedBy());
    }

    public void deleteLesson(int id) {
        lessonRepository.deleteById(id);
    }

    public void newLesson(LessonRequest lessonRequest) {
        Lesson lesson = new Lesson();
        Section section = sectionService.getSectionById(lessonRequest.getSectionId());
        copy(lesson, lessonRequest);
        lesson.setSection(section);
        lessonRepository.save(lesson);
    }

    public void updateLesson(Lesson lesson) {

        Lesson currentLesson = lessonRepository.findById(lesson.getId());
        currentLesson.setTitle(lesson.getTitle());
        lessonRepository.save(currentLesson);
    }


    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public List<LessonResponse> getAllLessonBySectionV1(int sectionId) {
        List<Lesson> lessons = lessonRepository.findBySectionId(sectionId);
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            LessonResponse lessonResponse = new LessonResponse();
            BeanUtils.copyProperties(lesson, lessonResponse);
            lessonResponses.add(lessonResponse);
        }
        return lessonResponses;
    }

    public LessonResponse getLessonByIdV1(int id) {
        Lesson lesson = lessonRepository.findById(id);
        LessonResponse lessonResponse = new LessonResponse();
        BeanUtils.copyProperties(lesson, lessonResponse);
        return lessonResponse;
    }
}

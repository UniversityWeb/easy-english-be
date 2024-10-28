package com.universityweb.lesson.service;


import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.section.entity.Section;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.LessonRepository;
import com.universityweb.section.service.SectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionService sectionService;

    public List<LessonResponse> getAllLessonBySection(LessonRequest lessonRequest) {
        List<Lesson> lessons = lessonRepository.findBySectionId(lessonRequest.getSectionId());
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            LessonResponse lessonResponse = new LessonResponse();
            BeanUtils.copyProperties(lesson, lessonResponse);
            lessonResponses.add(lessonResponse);
        }
        return lessonResponses;
    }

    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Lesson lesson = new Lesson();
        Optional<Section> sectionOptional = sectionService.getSectionById(lessonRequest.getSectionId());
        if (sectionOptional.isPresent()) {
            Section section = sectionOptional.get();
            lesson.setSection(section);
            BeanUtils.copyProperties(lessonRequest, lesson);
            lesson.setType(LessonType.valueOf(lessonRequest.getType()));
            LessonResponse lessonResponse = new LessonResponse();
            BeanUtils.copyProperties(lessonRepository.save(lesson), lessonResponse);
            lessonResponse.setSectionId(lesson.getSection().getId());

            return lessonResponse;

        } else {
            throw new RuntimeException("Section not found");
        }

    }

    public LessonResponse updateLesson(LessonRequest lessonRequest) {
        Optional<Lesson> currentLessonOptional = lessonRepository.findById(lessonRequest.getId());
        if (currentLessonOptional.isPresent()) {
            Lesson currentLesson = currentLessonOptional.get();
            BeanUtils.copyProperties(lessonRequest, currentLesson);
            LessonResponse lessonResponse = new LessonResponse();
            BeanUtils.copyProperties(lessonRepository.save(currentLesson), lessonResponse);
            lessonResponse.setSectionId(currentLesson.getSection().getId());
            return lessonResponse;
        } else {
            throw new RuntimeException("Lesson not found");
        }
    }

    public void deleteLesson(LessonRequest lessonRequest) {
        lessonRepository.deleteById(lessonRequest.getId());
    }

    public LessonResponse getLessonById(LessonRequest lessonRequest) {
        Lesson lesson = lessonRepository.findById(lessonRequest.getId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonResponse lessonResponse = new LessonResponse();
        BeanUtils.copyProperties(lesson, lessonResponse);
        return lessonResponse;
    }

}

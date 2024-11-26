package com.universityweb.lesson.service;


import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.drip.Drip;
import com.universityweb.drip.DripRepos;
import com.universityweb.lesson.LessonRepository;
import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lessontracker.LessonTrackerRepository;
import com.universityweb.section.entity.Section;
import com.universityweb.section.service.SectionService;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.entity.TestResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LessonServiceImpl
        extends BaseServiceImpl<Lesson, LessonResponse, Long, LessonRepository, LessonMapper>
        implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionService sectionService;
    private final LessonTrackerRepository lessonTrackerRepository;
    private final TestResultRepos testResultRepos;
    private final DripRepos dripRepos;

    @Autowired
    public LessonServiceImpl(
            LessonRepository repository,
            LessonMapper mapper,
            LessonRepository lessonRepository,
            SectionService sectionService,
            LessonTrackerRepository lessonTrackerRepository,
            TestResultRepos testResultRepos,
            DripRepos dripRepos
    ) {
        super(repository, mapper);
        this.lessonRepository = lessonRepository;
        this.sectionService = sectionService;
        this.lessonTrackerRepository = lessonTrackerRepository;
        this.testResultRepos = testResultRepos;
        this.dripRepos = dripRepos;
    }

    @Override
    public List<LessonResponse> getAllLessonBySection(String username, LessonRequest lessonRequest) {
        List<Lesson> lessons =  getAllLessonEntitiesBySection(username, lessonRequest.getSectionId());
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            Long lessonId = lesson.getId();
            boolean isLocked = this.isLocked(username, lessonId);
            LessonResponse lessonResponse = mapper.toDTOBasedOnIsLocked(isLocked, lesson);
            lessonResponses.add(lessonResponse);
        }
        return lessonResponses;
    }

    @Override
    public List<Lesson> getAllLessonEntitiesBySection(String username, Long sectionId) {
        return lessonRepository.findBySectionId(sectionId);
    }

    @Override
    public LessonResponse createLesson(LessonRequest lessonRequest) {
        Lesson lesson = new Lesson();
        Section section = sectionService.getEntityById(lessonRequest.getSectionId());
        lesson.setSection(section);
        BeanUtils.copyProperties(lessonRequest, lesson);
        lesson.setType(LessonType.valueOf(lessonRequest.getType()));
        LessonResponse lessonResponse = new LessonResponse();
        BeanUtils.copyProperties(lessonRepository.save(lesson), lessonResponse);
        lessonResponse.setSectionId(lesson.getSection().getId());

        return lessonResponse;
    }

    @Override
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
            throw new CustomException("Lesson not found");
        }
    }

    @Override
    public void deleteLesson(LessonRequest lessonRequest) {
        softDelete(lessonRequest.getId());
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("Lesson not found");
    }

    @Override
    public LessonResponse update(Long id, LessonResponse dto) {
        return null;
    }

    @Override
    public void softDelete(Long lessonId) {
        Lesson lesson = getEntityById(lessonId);
        lesson.setIsDeleted(true);
        lessonRepository.save(lesson);
    }

    private boolean isLocked(String username, Long lessonId) {
        boolean isCompleted = lessonTrackerRepository.isLessonCompleted(username, lessonId);
        if (!isCompleted) {
            return false;
        }

        // Step 2: Check for prerequisite lessons or tests using Drip
        List<Drip> drips = dripRepos.findDripByNextId(lessonId, Drip.ESourceType.TEST);
        for (Drip drip : drips) {
            if (drip.getPrevType() == Drip.ESourceType.LESSON) {
                if (!lessonTrackerRepository.isLessonCompleted(username, drip.getPrevId())) {
                    return true;
                }
            } else if (drip.getPrevType() == Drip.ESourceType.TEST) {
                if (!testResultRepos.isTestDone(username, drip.getPrevId())) {
                    return true;
                }
            }
        }

        return false;
    }
}

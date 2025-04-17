package com.universityweb.lesson.service;


import com.universityweb.common.exception.CustomException;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.drip.Drip;
import com.universityweb.drip.DripRepos;
import com.universityweb.drip.dto.PrevDripDTO;
import com.universityweb.lesson.LessonRepository;
import com.universityweb.lesson.customenum.LessonType;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lesson.request.LessonRequest;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lessontracker.LessonTrackerRepository;
import com.universityweb.section.entity.Section;
import com.universityweb.section.service.SectionService;
import com.universityweb.test.TestRepos;
import com.universityweb.test.entity.Test;
import com.universityweb.testresult.TestResultRepos;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl
        extends BaseServiceImpl<Lesson, LessonResponse, Long, LessonRepository, LessonMapper>
        implements LessonService {

    private final LessonRepository lessonRepository;
    private final SectionService sectionService;
    private final LessonTrackerRepository lessonTrackerRepository;
    private final TestResultRepos testResultRepos;
    private final DripRepos dripRepos;
    private final TestRepos testRepos;

    @Autowired
    public LessonServiceImpl(
            LessonRepository repository,
            LessonMapper mapper,
            LessonRepository lessonRepository,
            SectionService sectionService,
            LessonTrackerRepository lessonTrackerRepository,
            TestResultRepos testResultRepos,
            DripRepos dripRepos,
            TestRepos testRepos
    ) {
        super(repository, mapper);
        this.lessonRepository = lessonRepository;
        this.sectionService = sectionService;
        this.lessonTrackerRepository = lessonTrackerRepository;
        this.testResultRepos = testResultRepos;
        this.dripRepos = dripRepos;
        this.testRepos = testRepos;
    }

    @Override
    public List<LessonResponse> getAllLessonBySection(String username, LessonRequest lessonRequest) {
        return getAllLessonBySection(username, lessonRequest.getSectionId());
    }

    @Override
    public List<LessonResponse> getAllLessonBySection(String username, Long sectionId) {
        List<Lesson> lessons =  getAllLessonEntitiesBySection(username, sectionId);
        List<LessonResponse> lessonResponses = new ArrayList<>();
        for (Lesson lesson : lessons) {
            Long lessonId = lesson.getId();
            List<Drip> drips = dripRepos.findDripByNextId(lessonId, Drip.ESourceType.LESSON);

            boolean isLocked = this.isLocked(username, lessonId);
            LessonResponse lessonResponse = mapper.toDTOBasedOnIsLocked(isLocked, lesson);

            lessonResponse.setPrevDrips(drips.stream()
                    .map(drip -> {
                        Long prevId = drip.getPrevId();
                        String title = "";
                        String type = "";
                        switch (drip.getPrevType()) {
                            case LESSON:
                                Lesson tempLesson = lessonRepository.findById(prevId).orElse(null);
                                if (tempLesson != null) {
                                    title = tempLesson.getTitle() != null ? tempLesson.getTitle() : "Unnamed Lesson";
                                    type = tempLesson.getType() != null ? tempLesson.getType().toString() : "Unknown Lesson Type";
                                }
                                break;

                            case TEST:
                                Test tempTest = testRepos.findById(prevId).orElse(null);
                                if (tempTest != null) {
                                    title = tempTest.getTitle() != null ? tempTest.getTitle() : "Unnamed Test";
                                    type = Drip.ESourceType.TEST.toString();
                                }
                                break;
                        }
                        return new PrevDripDTO(drip.getPrevId(), title, type);
                    })
                    .collect(Collectors.toSet()));

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
            throw new ResourceNotFoundException("Lesson not found");
        }
    }

    @Override
    public void deleteLesson(LessonRequest lessonRequest) {
        softDelete(lessonRequest.getId());
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Lesson not found");
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
        if (isCompleted) {
            return false;
        }

        // Step 2: Check for prerequisite lessons or tests using Drip
        List<Drip> drips = dripRepos.findDripByNextId(lessonId, Drip.ESourceType.LESSON);
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

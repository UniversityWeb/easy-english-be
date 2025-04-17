package com.universityweb.lessontracker.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.exception.ResourceAlreadyExistsException;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.lessontracker.LessonTracker;
import com.universityweb.lessontracker.LessonTrackerMapper;
import com.universityweb.lessontracker.LessonTrackerRepository;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonTrackerServiceImpl
        extends BaseServiceImpl<LessonTracker, LessonTrackerDTO, Long, LessonTrackerRepository, LessonTrackerMapper>
        implements LessonTrackerService {
    private final UserService userService;
    private final LessonService lessonService;
    private final SectionService sectionService;
    private final LessonMapper lessonMapper;
    private final EnrollmentService enrollmentService;

    @Autowired
    public LessonTrackerServiceImpl(
            LessonTrackerRepository repository,
            LessonTrackerMapper mapper,
            UserService userService,
            LessonService lessonService,
            SectionService sectionService,
            LessonMapper lessonMapper,
            EnrollmentService enrollmentService
    ) {
        super(repository, mapper);
        this.userService = userService;
        this.lessonService = lessonService;
        this.sectionService = sectionService;
        this.lessonMapper = lessonMapper;
        this.enrollmentService = enrollmentService;
    }

    @Override
    protected void checkBeforeAdd(LessonTrackerDTO dto) {
        String username = dto.getUsername();
        Long lessonId = dto.getLessonId();
        Optional<LessonTracker> optionalLessonTracker = repository.findByUser_UsernameAndLesson_Id(username, lessonId);
        if (optionalLessonTracker.isPresent()) {
            throw new ResourceAlreadyExistsException("LessonTracker already exists");
        }
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(LessonTracker entity, LessonTrackerDTO dto) {
        User user = userService.loadUserByUsername(dto.getUsername());
        Lesson lesson = lessonService.getEntityById(dto.getLessonId());

        entity.setUser(user);
        entity.setLesson(lesson);
        entity.setIsDeleted(false);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Could not find LessonTracker with id " + id);
    }

    @Override
    public LessonTrackerDTO update(Long id, LessonTrackerDTO dto) {
        LessonTracker lessonTracker = getEntityById(id);

        lessonTracker.setIsCompleted(dto.getIsCompleted());
        lessonTracker.setCompletedAt(dto.getCompletedAt());

        return savedAndConvertToDTO(lessonTracker);
    }

    @Override
    public void softDelete(Long id) {
        LessonTracker lessonTracker = getEntityById(id);

        lessonTracker.setIsDeleted(true);
        repository.save(lessonTracker);
    }

    @Override
    public Boolean isLearned(String username, Long lessonId) {
        return repository.isLessonCompleted(username, lessonId);
    }

    @Override
    public LessonTracker getByUsernameAndLessonId(String username, Long targetId) {
        return repository.findByUser_UsernameAndLesson_Id(username, targetId)
                .orElse(null);
    }

    @Override
    public LessonResponse getFirstUnlearnedLesson(String username, Long courseId) {
        enrollmentService.isEnrolled(username, courseId);

        List<Section> sections = sectionService.getAllSectionEntitiesByCourse(courseId);
        for (Section section : sections) {
            Long sectionId = section.getId();
            List<Lesson> lessons = lessonService.getAllLessonEntitiesBySection(username, sectionId);

            for (Lesson lesson : lessons) {
                boolean isLearned = isLearned(username, lesson.getId());
                if (!isLearned) {
                    return lessonMapper.toDTO(lesson);
                }
            }
        }
        return null;
    }
}

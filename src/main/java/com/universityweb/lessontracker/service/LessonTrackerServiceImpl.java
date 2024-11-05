package com.universityweb.lessontracker.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.lessontracker.LessonTracker;
import com.universityweb.lessontracker.LessonTrackerMapper;
import com.universityweb.lessontracker.LessonTrackerRepository;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LessonTrackerServiceImpl
        extends BaseServiceImpl<LessonTracker, LessonTrackerDTO, Long, LessonTrackerRepository, LessonTrackerMapper>
        implements LessonTrackerService {
    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public LessonTrackerServiceImpl(
            LessonTrackerRepository repository,
            LessonTrackerMapper mapper,
            UserService userService,
            LessonService lessonService
    ) {
        super(repository, mapper);
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @Override
    protected void checkBeforeAdd(LessonTrackerDTO dto) {
        String username = dto.getUsername();
        Long lessonId = dto.getLessonId();
        Optional<LessonTracker> optionalLessonTracker = repository.findByUser_UsernameAndLesson_Id(username, lessonId);
        if (optionalLessonTracker.isPresent()) {
            throw new RuntimeException("LessonTracker already exists");
        }
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(LessonTracker entity, LessonTrackerDTO dto) {
        User user = userService.loadUserByUsername(dto.getUsername());
        Lesson lesson = lessonService.getEntityById(dto.getLessonId());

        entity.setUser(user);
        entity.setLesson(lesson);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find LessonTracker with id " + id);
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
        Optional<LessonTracker> optional = repository.findByUser_UsernameAndLesson_Id(username, lessonId);
        if (optional.isEmpty()) {
            return false;
        }
        LessonTracker lessonTracker = optional.get();
        return lessonTracker.getIsCompleted() && !lessonTracker.getIsDeleted();
    }

    @Override
    public LessonTracker getByUsernameAndLessonId(String username, Long targetId) {
        return repository.findByUser_UsernameAndLesson_Id(username, targetId)
                .orElse(null);
    }
}

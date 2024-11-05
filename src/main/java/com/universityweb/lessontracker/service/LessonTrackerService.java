package com.universityweb.lessontracker.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.lessontracker.LessonTracker;
import com.universityweb.lessontracker.dto.LessonTrackerDTO;

public interface LessonTrackerService extends BaseService<LessonTracker, LessonTrackerDTO, Long> {
    Boolean isLearned(String username, Long lessonId);

    LessonTracker getByUsernameAndLessonId(String username, Long targetId);
}

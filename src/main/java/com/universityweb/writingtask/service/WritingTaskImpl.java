package com.universityweb.writingtask.service;

import com.universityweb.common.auth.exception.UserNotFoundException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.writingtask.WritingTaskMapper;
import com.universityweb.writingtask.WritingTaskRepos;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WritingTaskImpl
        extends BaseServiceImpl<WritingTask, WritingTaskDTO, Long, WritingTaskRepos, WritingTaskMapper>
        implements WritingTaskService {

    private final UserService userService;
    private final LessonService lessonService;

    @Autowired
    public WritingTaskImpl(
            WritingTaskRepos repository,
            WritingTaskMapper mapper,
            UserService userService,
            LessonService lessonService
    ) {
        super(repository, mapper);
        this.userService = userService;
        this.lessonService = lessonService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any writing tasks with id=" + id);
    }

    @Override
    protected void checkBeforeAdd(WritingTaskDTO dto) {
        Long sectionId = dto.getSectionId();

        lessonService.getEntityById(sectionId);
    }
}

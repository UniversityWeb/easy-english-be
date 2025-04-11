package com.universityweb.writingtask.service;

import com.universityweb.common.auth.exception.UserNotFoundException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
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

    @Autowired
    public WritingTaskImpl(
            WritingTaskRepos repository,
            WritingTaskMapper mapper,
            UserService userService
    ) {
        super(repository, mapper);
        this.userService = userService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any writing tasks with id=" + id);
    }

    @Override
    protected void checkBeforeAdd(WritingTaskDTO dto) {
        String ownerUsername = dto.getOwnerUsername();
        String teacherUsername = dto.getTeacherUsername();

        if (!userService.existsByUsername(ownerUsername)) {
            throw new UserNotFoundException("Owner username not found: " + ownerUsername);
        }

        if (teacherUsername != null && !userService.existsByUsername(teacherUsername)) {
            throw new UserNotFoundException("Teacher username not found: " + teacherUsername);
        }
    }
}

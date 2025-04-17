package com.universityweb.writingtask.service;

import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.writingtask.WritingTaskMapper;
import com.universityweb.writingtask.WritingTaskRepos;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.req.WritingTaskFilterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WritingTaskServiceImpl
        extends BaseServiceImpl<WritingTask, WritingTaskDTO, Long, WritingTaskRepos, WritingTaskMapper>
        implements WritingTaskService {

    private final LessonService lessonService;

    @Autowired
    public WritingTaskServiceImpl(
            WritingTaskRepos repository,
            WritingTaskMapper mapper,
            LessonService lessonService
    ) {
        super(repository, mapper);
        this.lessonService = lessonService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Could not find any writing tasks with id=" + id);
    }

    @Override
    protected void checkBeforeAdd(WritingTaskDTO dto) {
        Long sectionId = dto.getSectionId();

        lessonService.getEntityById(sectionId);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(WritingTask entity, WritingTaskDTO dto) {
        if (entity.getStatus() == null) {
            entity.setStatus(WritingTask.EStatus.DRAFT);
        }
    }

    @Override
    public WritingTaskDTO update(Long id, WritingTaskDTO dto) {
        WritingTask curEntity = getEntityById(id);
        mapper.updateEntityFromDTO(dto, curEntity);
        return savedAndConvertToDTO(curEntity);
    }

    @Override
    public void softDelete(Long id) {
        WritingTask curEntity = getEntityById(id);
        curEntity.setStatus(WritingTask.EStatus.DELETED);
        save(curEntity);
    }

    @Override
    public Page<WritingTaskDTO> getByFilters(WritingTaskFilterReq filterReq) {
        Pageable pageable = PageRequest.of(filterReq.getPageNumber(), filterReq.getSize());

        Page<WritingTask> tasksPage = repository.findByFilters(filterReq.getSectionId(),
                filterReq.getTitle(), filterReq.getLevel(), filterReq.getStatus(), pageable);

        return mapper.mapPageToPageDTO(tasksPage);
    }
}

package com.universityweb.writingresult.service;

import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.writingresult.WritingResultMapper;
import com.universityweb.writingresult.WritingResultRepos;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingresult.req.WritingResultFilterReq;
import com.universityweb.writingtask.service.WritingTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WritingResultServiceImpl
        extends BaseServiceImpl<WritingResult, WritingResultDTO, Long, WritingResultRepos, WritingResultMapper>
        implements WritingResultService {

    private final WritingTaskService writingTaskService;
    private final UserService userService;

    @Autowired
    public WritingResultServiceImpl(
            WritingResultRepos repository,
            WritingResultMapper mapper,
            WritingTaskService writingTaskService,
            UserService userService
    ) {
        super(repository, mapper);
        this.writingTaskService = writingTaskService;
        this.userService = userService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Could not find writing result with id " + id);
    }

    @Override
    protected void checkBeforeAdd(WritingResultDTO dto) {
        Long writingTaskId = dto.getWritingTaskId();
        String ownerUsername = dto.getOwnerUsername();

        writingTaskService.getEntityById(writingTaskId);
        userService.loadUserByUsername(ownerUsername);
    }

    @Override
    protected void postCreate(WritingResult savedEntity) {
        super.postCreate(savedEntity);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(WritingResult entity, WritingResultDTO dto) {
        if (entity.getStatus() == null) {
            entity.setStatus(WritingResult.EStatus.SUBMITTED);
        }
    }

    @Override
    public WritingResultDTO update(Long id, WritingResultDTO dto) {
        WritingResult curEntity = getEntityById(id);
        mapper.updateEntityFromDTO(dto, curEntity);
        return savedAndConvertToDTO(curEntity);
    }

    @Override
    public void delete(Long id) {
        WritingResult curEntity = getEntityById(id);
        curEntity.setStatus(WritingResult.EStatus.DELETED);
        save(curEntity);
    }

    @Override
    public Page<WritingResultDTO> getByFilters(WritingResultFilterReq filterReq) {
        Pageable pageable = PageRequest.of(filterReq.getPageNumber(), filterReq.getSize());

        Page<WritingResult> tasksPage = repository.findByFilters(filterReq.getWritingTaskId(),
                filterReq.getOwnerUsername(), filterReq.getStatus(), pageable);

        return mapper.mapPageToPageDTO(tasksPage);
    }

    @Override
    public Page<WritingResultDTO> getWritingResults(WritingResultFilterReq req) {
        Pageable pageable = PageRequest.of(req.getPageNumber(), req.getSize());

        Page<WritingResult> tasksPage = repository.findByFilters(req.getSectionId(),
                req.getWritingTaskId(), req.getOwnerUsername(), req.getStatus(), pageable);

        return mapper.mapPageToPageDTO(tasksPage);
    }
}

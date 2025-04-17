package com.universityweb.writingresult.service;

import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.writingresult.WritingResultMapper;
import com.universityweb.writingresult.WritingResultRepos;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingresult.req.WritingResultFilterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class WritingResultServiceImpl
        extends BaseServiceImpl<WritingResult, WritingResultDTO, Long, WritingResultRepos, WritingResultMapper>
        implements WritingResultService {

    @Autowired
    public WritingResultServiceImpl(WritingResultRepos repository, WritingResultMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new ResourceNotFoundException("Could not find writing result with id " + id);
    }

    @Override
    public Page<WritingResultDTO> getByFilters(WritingResultFilterReq filterReq) {
        return null;
    }
}

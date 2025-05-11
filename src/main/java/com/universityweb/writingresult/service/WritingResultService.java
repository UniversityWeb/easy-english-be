package com.universityweb.writingresult.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import com.universityweb.writingresult.req.WritingResultFilterReq;
import org.springframework.data.domain.Page;

public interface WritingResultService extends BaseService<WritingResult, WritingResultDTO, Long> {
    Page<WritingResultDTO> getByFilters(WritingResultFilterReq filterReq);
}

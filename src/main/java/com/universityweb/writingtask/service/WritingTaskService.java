package com.universityweb.writingtask.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import com.universityweb.writingtask.req.WritingTaskFilterReq;
import org.springframework.data.domain.Page;

public interface WritingTaskService extends BaseService<WritingTask, WritingTaskDTO, Long> {
    Page<WritingTaskDTO> getByFilters(WritingTaskFilterReq filterReq);
}

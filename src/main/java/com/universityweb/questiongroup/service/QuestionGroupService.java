package com.universityweb.questiongroup.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;

import java.util.List;

public interface QuestionGroupService extends BaseService<QuestionGroup, QuestionGroupDTO, Long> {
    List<QuestionGroupDTO> getByTestPartId(Long testPartId);
}

package com.universityweb.useranswer;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import com.universityweb.useranswer.entity.UserAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAnswerMapper extends BaseMapper<UserAnswer, UserAnswerDTO> {
    @Mapping(source = "testQuestion.id", target = "testQuestionId")
    @Mapping(source = "testResult.id", target = "testResultId")
    @Override
    UserAnswerDTO toDTO(UserAnswer entity);

    @Mapping(target = "testResult", ignore = true)
    @Override
    UserAnswer toEntity(UserAnswerDTO dto);
}

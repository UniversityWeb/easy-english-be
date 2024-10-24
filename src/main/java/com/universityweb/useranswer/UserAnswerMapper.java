package com.universityweb.useranswer;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.useranswer.dto.UserAnswerDTO;
import com.universityweb.useranswer.entity.UserAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAnswerMapper extends BaseMapper<UserAnswer, UserAnswerDTO> {
    UserAnswerMapper INSTANCE = Mappers.getMapper(UserAnswerMapper.class);

    @Mapping(source = "testQuestion.id", target = "testQuestionId")
    @Mapping(source = "testResult.id", target = "testResultId")
    @Override
    UserAnswerDTO toDTO(UserAnswer entity);

    @Override
    List<UserAnswerDTO> toDTOs(List<UserAnswer> entities);

    @Mapping(target = "testQuestion", ignore = true)
    @Mapping(target = "testResult", ignore = true)
    @Override
    UserAnswer toEntity(UserAnswerDTO dto);

    @Override
    List<UserAnswer> toEntities(List<UserAnswerDTO> dtos);
}

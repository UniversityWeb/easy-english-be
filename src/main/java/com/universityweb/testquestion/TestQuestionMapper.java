package com.universityweb.testquestion;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestQuestionMapper extends BaseMapper<TestQuestion, TestQuestionDTO> {
    TestQuestionMapper INSTANCE = Mappers.getMapper(TestQuestionMapper.class);

    @Mapping(source = "questionGroup.id", target = "questionGroupId")
    @Override
    TestQuestionDTO toDTO(TestQuestion entity);

    @Override
    List<TestQuestionDTO> toDTOs(List<TestQuestion> entities);

    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(target = "userAnswers", ignore = true)
    @Override
    TestQuestion toEntity(TestQuestionDTO dto);

    @Override
    List<TestQuestion> toEntities(List<TestQuestionDTO> dtos);
}

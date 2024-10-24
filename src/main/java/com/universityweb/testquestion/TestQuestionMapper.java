package com.universityweb.testquestion;

import com.universityweb.testquestion.dto.TestQuestionDTO;
import com.universityweb.testquestion.entity.TestQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestQuestionMapper {
    TestQuestionMapper INSTANCE = Mappers.getMapper(TestQuestionMapper.class);

    @Mapping(source = "questionGroup.id", target = "questionGroupId")
    TestQuestionDTO toDTO(TestQuestion testQuestion);

    List<TestQuestionDTO> toDTOs(List<TestQuestion> testQuestions);

    @Mapping(target = "questionGroup", ignore = true)
    @Mapping(target = "userAnswers", ignore = true)
    TestQuestion toEntity(TestQuestionDTO testQuestionDTO);

    List<TestQuestion> toEntities(List<TestQuestionDTO> testQuestionDTOs);
}

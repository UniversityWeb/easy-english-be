package com.universityweb.questiongroup;

import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper {
    QuestionGroupMapper INSTANCE = Mappers.getMapper(QuestionGroupMapper.class);

    @Mapping(source = "testPart.id", target = "testPartId")
    QuestionGroupDTO toDTO(QuestionGroup questionGroup);

    List<QuestionGroupDTO> toDTOs(List<QuestionGroup> questionGroups);

    @Mapping(target = "testPart", ignore = true)
    QuestionGroup toEntity(QuestionGroupDTO questionGroupDTO);

    List<QuestionGroup> toEntities(List<QuestionGroupDTO> questionGroupDTOs);
}

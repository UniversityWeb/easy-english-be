package com.universityweb.questiongroup;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionGroupMapper extends BaseMapper<QuestionGroup, QuestionGroupDTO> {
    @Mapping(source = "testPart.id", target = "testPartId")
    @Override
    QuestionGroupDTO toDTO(QuestionGroup questionGroup);

    @Mapping(target = "testPart", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Override
    QuestionGroup toEntity(QuestionGroupDTO questionGroupDTO);
}

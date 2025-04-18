package com.universityweb.writingresult;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.writingresult.dto.WritingResultDTO;
import com.universityweb.writingresult.entity.WritingResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WritingResultMapper extends BaseMapper<WritingResult, WritingResultDTO> {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Override
    void updateEntityFromDTO(WritingResultDTO dto, @MappingTarget WritingResult entity);
}

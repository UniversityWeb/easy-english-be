package com.universityweb.writingtask;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WritingTaskMapper extends BaseMapper<WritingTask, WritingTaskDTO> {

    @Override
    WritingTaskDTO toDTO(WritingTask entity);

    @Override
    WritingTask toEntity(WritingTaskDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sectionId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Override
    void updateEntityFromDTO(WritingTaskDTO dto, @MappingTarget WritingTask entity);
}

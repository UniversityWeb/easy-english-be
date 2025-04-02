package com.universityweb.writingtask;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.writingtask.dto.WritingTaskDTO;
import com.universityweb.writingtask.entity.WritingTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WritingTaskMapper extends BaseMapper<WritingTask, WritingTaskDTO> {

    @Override
    WritingTaskDTO toDTO(WritingTask entity);

    @Override
    WritingTask toEntity(WritingTaskDTO dto);
}

package com.universityweb.testpart.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestPartMapper extends BaseMapper<TestPart, TestPartDTO> {
    TestPartMapper INSTANCE = Mappers.getMapper(TestPartMapper.class);

    @Mapping(source = "test.id", target = "testId")
    TestPartDTO toDTO(TestPart entity);
    List<TestPartDTO> toDTOs(List<TestPart> entities);

    @Mapping(target = "test", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    TestPart toEntity(TestPartDTO dto);
    List<TestPart> toEntities(List<TestPartDTO> dtos);
}

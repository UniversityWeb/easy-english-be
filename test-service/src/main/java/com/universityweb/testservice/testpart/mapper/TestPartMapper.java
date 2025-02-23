package com.universityweb.testservice.testpart.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestPartMapper extends BaseMapper<TestPart, TestPartDTO> {
    @Mapping(source = "test.id", target = "testId")
    @Override
    TestPartDTO toDTO(TestPart entity);

    @Mapping(target = "test", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Override
    TestPart toEntity(TestPartDTO dto);
}

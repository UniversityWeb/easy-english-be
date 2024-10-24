package com.universityweb.test;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper extends BaseMapper<Test, TestDTO> {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(source = "section.id", target = "sectionId")
    TestDTO toDTO(Test test);
    List<TestDTO> toDTOs(List<Test> tests);

    @Mapping(target = "section", ignore = true)
    Test toEntity(TestDTO testDTO);
    List<Test> toEntities(List<TestDTO> testDTOs);
}

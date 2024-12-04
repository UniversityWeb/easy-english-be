package com.universityweb.test;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper extends BaseMapper<Test, TestDTO> {
    @Mapping(source = "section.id", target = "sectionId")
    TestDTO toDTO(Test test);

    @Mapping(target = "section", ignore = true)
    Test toEntity(TestDTO testDTO);

    @Named("toLockedDTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "test.id", target = "id")
    @Mapping(source = "test.title", target = "title")
    @Mapping(source = "test.type", target = "type")
    @Mapping(source = "test.section.id", target = "sectionId")
    TestDTO toLockedDTO(Test test);

    default TestDTO toDTOBasedOnIsLocked(boolean isLocked, Test test) {
        if (isLocked) {
            TestDTO lockedDTO = toLockedDTO(test);
            lockedDTO.setLocked(true);
            return lockedDTO;
        } else {
            TestDTO fullDTO = toDTO(test);
            fullDTO.setLocked(false);
            return fullDTO;
        }
    }
}

package com.universityweb.testresult.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.dto.TestResultWithoutListDTO;
import com.universityweb.testresult.entity.TestResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestResultMapper extends BaseMapper<TestResult, TestResultDTO> {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "test.id", target = "testId")
    @Mapping(target = "courseId", ignore = true)
    TestResultDTO toDTO(TestResult entity);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "test.id", target = "testId")
    @Mapping(target = "courseId", ignore = true)
    TestResultWithoutListDTO toTestResultWithoutListDTO(TestResult entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "test", ignore = true)
    TestResult toEntity(TestResultDTO dto);
}

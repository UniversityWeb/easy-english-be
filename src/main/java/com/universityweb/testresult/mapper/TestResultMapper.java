package com.universityweb.testresult.mapper;

import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.request.AddTestResultRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestResultMapper {
    TestResultMapper INSTANCE = Mappers.getMapper(TestResultMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "test.id", target = "testId")
    TestResultDTO toDTO(TestResult entity);
    List<TestResultDTO> toDTOs(List<TestResult> entities);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "test", ignore = true)
    TestResult toEntity(TestResultDTO dto);
    List<TestResult> toEntities(List<TestResultDTO> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "test", ignore = true)
    TestResult toEntity(AddTestResultRequest addTestResultRequest);
}

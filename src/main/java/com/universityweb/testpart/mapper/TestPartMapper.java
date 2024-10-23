package com.universityweb.testpart.mapper;

import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.request.AddTestPartRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestPartMapper {
    TestPartMapper INSTANCE = Mappers.getMapper(TestPartMapper.class);

    @Mapping(source = "test.id", target = "testId")
    TestPartDTO toDTO(TestPart entity);
    List<TestPartDTO> toDTOs(List<TestPart> entities);

    @Mapping(target = "test", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    TestPart toEntity(TestPartDTO dto);
    List<TestPart> toEntities(List<TestPartDTO> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "test", ignore = true)
    @Mapping(target = "questions", ignore = true)
    TestPart toEntity(AddTestPartRequest addTestPartRequest);
}

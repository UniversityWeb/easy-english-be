package com.universityweb.testpart.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.test.service.TestService;
import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.mapper.TestPartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestPartServiceImpl 
        extends BaseServiceImpl<TestPart, TestPartDTO, Long, TestPartRepos, TestPartMapper> 
        implements TestPartService {

    private final TestService testService;

    @Autowired
    protected TestPartServiceImpl(TestPartRepos repository, TestService testService) {
        super(repository, TestPartMapper.INSTANCE);
        this.testService = testService;
    }

    @Override
    public List<TestPartDTO> getTestPartsByTestId(Long testId) {
        Sort sort = Sort.by(Sort.Order.asc("ordinalNumber"));
        List<TestPart> testParts = repository.findByTestId(testId, sort);
        return mapper.toDTOs(testParts);
    }

    @Override
    public TestPartDTO update(Long id, TestPartDTO dto) {
        TestPart existingTestPart = getEntityById(id);

        existingTestPart.setTitle(dto.title());
        existingTestPart.setOrdinalNumber(dto.ordinalNumber());

        TestPart updatedTestPart = repository.save(existingTestPart);
        return mapper.toDTO(updatedTestPart);
    }

    @Override
    public void softDelete(Long id) {
        super.softDelete(id);

        TestPart existingTestPart = getEntityById(id);
        existingTestPart.setIsDeleted(true);
        repository.save(existingTestPart);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any test parts with id" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(TestPart entity, TestPartDTO dto) {
        entity.setIsDeleted(false);
        entity.setTest(testService.getEntityById(dto.testId()));
    }
}

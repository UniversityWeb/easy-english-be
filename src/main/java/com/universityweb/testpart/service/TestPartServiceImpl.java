package com.universityweb.testpart.service;

import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.mapper.TestPartMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestPartServiceImpl 
        extends BaseServiceImpl<TestPart, TestPartDTO, Long, TestPartRepos, TestPartMapper> 
        implements TestPartService {

    private final TestService testService;

    @Autowired
    protected TestPartServiceImpl(
            TestPartRepos repository,
            TestPartMapper mapper,
            TestService testService
    ) {
        super(repository, mapper);
        this.testService = testService;
    }

    @Override
    public List<TestPartDTO> getTestPartsByTestId(Long testId) {
        Sort sort = Sort.by(Sort.Order.asc("ordinalNumber"));
        List<TestPart> testParts = repository.findByTestId(testId, sort);
        return mapper.toDTOs(testParts);
    }

    @Override
    public @NotNull TestPart getFirstOrCreateTestPartByTestId(Long testId) {
        TestPart testPart = repository.getFirstTestPartByTestId(testId);
        if (testPart != null) {
            return testPart;
        }

        Test test = testService.getEntityById(testId);
        TestPart newTestPart = TestPart.builder()
                .title("Default Title")
                .ordinalNumber(1)
                .isDeleted(false)
                .test(test)
                .build();
        return repository.save(newTestPart);
    }

    @Override
    @Transactional
    public void swapTwoParts(Long partId1, Long partId2) {
        TestPart part1 = getEntityById(partId1);
        TestPart part2 = getEntityById(partId2);

        Integer tempOrdinalNumber = part1.getOrdinalNumber();
        part1.setOrdinalNumber(part2.getOrdinalNumber());
        part2.setOrdinalNumber(tempOrdinalNumber);

        repository.save(part1);
        repository.save(part2);
    }

    @Override
    public TestPartDTO update(Long id, TestPartDTO dto) {
        TestPart existingTestPart = getEntityById(id);

        existingTestPart.setTitle(dto.title());
        existingTestPart.setReadingPassage(dto.readingPassage());

        return savedAndConvertToDTO(existingTestPart);
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
        throw new CustomException("Could not find any test parts with id" + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(TestPart entity, TestPartDTO dto) {
        entity.setIsDeleted(false);
        entity.setTest(testService.getEntityById(dto.testId()));
    }
}

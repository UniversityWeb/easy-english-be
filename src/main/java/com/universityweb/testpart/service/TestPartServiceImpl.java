package com.universityweb.testpart.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.review.service.ReviewServiceImpl;
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

import java.util.List;

@Service
public class TestPartServiceImpl 
        extends BaseServiceImpl<TestPart, TestPartDTO, Long, TestPartRepos, TestPartMapper> 
        implements TestPartService {

    private final TestService testService;
    private final ReviewServiceImpl reviewServiceImpl;

    @Autowired
    protected TestPartServiceImpl(TestPartRepos repository, TestService testService, ReviewServiceImpl reviewServiceImpl) {
        super(repository, TestPartMapper.INSTANCE);
        this.testService = testService;
        this.reviewServiceImpl = reviewServiceImpl;
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
    public TestPartDTO update(Long id, TestPartDTO dto) {
        TestPart existingTestPart = getEntityById(id);

        existingTestPart.setTitle(dto.title());
        existingTestPart.setReadingPassage(dto.readingPassage());
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

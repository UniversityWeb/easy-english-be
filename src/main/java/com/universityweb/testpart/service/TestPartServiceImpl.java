package com.universityweb.testpart.service;

import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.mapper.TestPartMapper;
import com.universityweb.testpart.request.AddTestPartRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestPartServiceImpl implements TestPartService {
    private final TestPartMapper testPartMapper = TestPartMapper.INSTANCE;

    @Autowired
    private TestPartRepos testPartRepos;

    @Override
    public TestPartDTO createTestPart(AddTestPartRequest addTestPartRequest) {
        TestPart testPart = testPartMapper.toEntity(addTestPartRequest);
        TestPart savedTestPart = testPartRepos.save(testPart);
        return testPartMapper.toDTO(savedTestPart);
    }

    @Override
    public TestPartDTO updateTestPart(TestPartDTO testPartDTO) {
        TestPart existingTestPart = getEntityById(testPartDTO.id());

        existingTestPart.setTitle(testPartDTO.title());
        existingTestPart.setOrdinalNumber(testPartDTO.ordinalNumber());

        TestPart updatedTestPart = testPartRepos.save(existingTestPart);
        return testPartMapper.toDTO(updatedTestPart);
    }

    @Override
    public TestPartDTO getTestPartById(Long id) {
        TestPart testPart = getEntityById(id);
        return testPartMapper.toDTO(testPart);
    }

    @Override
    public List<TestPartDTO> getTestPartsByTestId(Long testId) {
        List<TestPart> testParts = testPartRepos.findByTestId(testId);
        return testParts.stream()
                .map(testPartMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTestPart(Long id) {
        TestPart testPart = getEntityById(id);
        testPart.setIsDeleted(true); 
        testPartRepos.save(testPart); 
    }

    @Override
    public TestPart getEntityById(Long id) {
        return testPartRepos.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Could not find any test parts with id" + id));
    }
}

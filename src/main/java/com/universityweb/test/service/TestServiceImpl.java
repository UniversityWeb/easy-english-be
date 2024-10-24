package com.universityweb.test.service;

import com.universityweb.test.TestMapper;
import com.universityweb.test.TestRepos;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    private final TestMapper testMapper = TestMapper.INSTANCE;

    @Autowired
    private TestRepos testRepos;

    @Override
    public List<TestDTO> getAllTests() {
        List<Test> tests = testRepos.findAll();
        return testMapper.toDTOs(tests);
    }

    @Override
    public TestDTO getTestById(Long id) {
        Test test = getEntityById(id);
        return testMapper.toDTO(test);
    }

    @Override
    public TestDTO createTest(TestDTO testDTO) {
        Test test = testMapper.toEntity(testDTO);
        Test savedTest = testRepos.save(test);
        return testMapper.toDTO(savedTest);
    }

    @Override
    public TestDTO updateTest(TestDTO testDTO) {
        Test existingTest = getEntityById(testDTO.getId());

        existingTest.setStatus(testDTO.getStatus());
        existingTest.setTitle(testDTO.getTitle());
        existingTest.setDescription(testDTO.getDescription());
        existingTest.setOrdinalNumber(testDTO.getOrdinalNumber());
        existingTest.setDurationInMilis(testDTO.getDurationInMilis());
        existingTest.setStartDate(testDTO.getStartDate());
        existingTest.setEndDate(testDTO.getEndDate());

        Test updatedTest = testRepos.save(existingTest);
        return testMapper.toDTO(updatedTest);
    }

    @Override
    public void deleteTest(Long id) {
        updateStatus(id, Test.EStatus.DELETED);
    }

    @Override
    public void updateStatus(Long id, Test.EStatus status) {
        Test existingTest = getEntityById(id);
        existingTest.setStatus(status);
        testRepos.save(existingTest);
    }

    @Override
    public Test getEntityById(Long id) {
        return testRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find any tests with id=" + id));
    }
}

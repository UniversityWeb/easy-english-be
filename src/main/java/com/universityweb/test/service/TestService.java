package com.universityweb.test.service;

import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;

import java.util.List;

public interface TestService {
    List<TestDTO> getAllTests();
    TestDTO getTestById(Long id);
    TestDTO createTest(TestDTO testDTO);
    TestDTO updateTest(TestDTO testDTO);
    void deleteTest(Long id);
    void updateStatus(Long id, Test.EStatus status);
    Test getEntityById(Long id);
}

package com.universityweb.testpart.service;

import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.request.AddTestPartRequest;

import java.util.List;

public interface TestPartService {
    TestPartDTO createTestPart(AddTestPartRequest addTestPartRequest);
    TestPartDTO updateTestPart(TestPartDTO testPartDTO);
    TestPartDTO getTestPartById(Long id);
    List<TestPartDTO> getTestPartsByTestId(Long testId);
    void deleteTestPart(Long id);
    TestPart getEntityById(Long id);
}

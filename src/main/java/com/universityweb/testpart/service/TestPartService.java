package com.universityweb.testpart.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.testpart.dto.TestPartDTO;
import com.universityweb.testpart.entity.TestPart;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface TestPartService extends BaseService<TestPart, TestPartDTO, Long> {
    List<TestPartDTO> getTestPartsByTestId(Long testId);
    @NotNull TestPart getFirstOrCreateTestPartByTestId(Long testId);
    void swapTwoParts(Long partId1, Long partId2);
}

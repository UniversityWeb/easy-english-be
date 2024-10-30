package com.universityweb.test.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;

import java.util.List;

public interface TestService extends BaseService<Test, TestDTO, Long> {
    void updateStatus(Long id, Test.EStatus status);
    List<TestDTO> getBySection(Long sectionId);
}

package com.universityweb.test.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.test.TestMapper;
import com.universityweb.test.TestRepos;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl extends BaseServiceImpl<Test, TestDTO, Long, TestRepos, TestMapper>
        implements TestService {


    @Autowired
    public TestServiceImpl(TestRepos repository) {
        super(repository, TestMapper.INSTANCE);
    }

    @Override
    public void updateStatus(Long id, Test.EStatus status) {
        Test existingTest = getEntityById(id);
        existingTest.setStatus(status);
        repository.save(existingTest);
    }

    @Override
    public TestDTO update(Long id, TestDTO dto) {
        Test existingTest = getEntityById(dto.getId());

        existingTest.setStatus(dto.getStatus());
        existingTest.setTitle(dto.getTitle());
        existingTest.setDescription(dto.getDescription());
        existingTest.setOrdinalNumber(dto.getOrdinalNumber());
        existingTest.setDurationInMilis(dto.getDurationInMilis());
        existingTest.setStartDate(dto.getStartDate());
        existingTest.setEndDate(dto.getEndDate());

        Test updatedTest = repository.save(existingTest);
        return mapper.toDTO(updatedTest);
    }

    @Override
    public void softDelete(Long id) {
        super.softDelete(id);

        updateStatus(id, Test.EStatus.DELETED);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any tests with id=" + id);
    }
}

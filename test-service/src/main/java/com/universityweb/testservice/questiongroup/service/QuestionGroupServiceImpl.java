package com.universityweb.testservice.questiongroup.service;

import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import com.universityweb.testquestion.TestQuestionRepos;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionGroupServiceImpl
        extends BaseServiceImpl<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupRepos, QuestionGroupMapper>
        implements QuestionGroupService {

    private TestPartService testPartService;
    private TestQuestionRepos testQuestionRepos;

    @Autowired
    public QuestionGroupServiceImpl(
            QuestionGroupRepos repository,
            QuestionGroupMapper mapper,
            TestPartService testPartService,
            TestQuestionRepos testQuestionRepos
    ) {
        super(repository, mapper);
        this.testPartService = testPartService;
        this.testQuestionRepos = testQuestionRepos;
    }

    @Override
    public QuestionGroupDTO update(Long id, QuestionGroupDTO dto) {
        QuestionGroup existingQuestionGroup = getEntityById(id);

        existingQuestionGroup.setRequirement(dto.getRequirement());

        return savedAndConvertToDTO(existingQuestionGroup);
    }

    @Override
    public void softDelete(Long id) {
        super.softDelete(id);

        QuestionGroup existingQuestionGroup = getEntityById(id);
        existingQuestionGroup.setIsDeleted(true);
        repository.save(existingQuestionGroup);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("QuestionGroup not found with ID: " + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(QuestionGroup entity, QuestionGroupDTO dto) {
        entity.setIsDeleted(false);
        TestPart testPart = testPartService.getEntityById(dto.getTestPartId());
        entity.setTestPart(testPart);
    }

    @Override
    public List<QuestionGroupDTO> getByTestPartId(Long testPartId) {
        List<QuestionGroup> questionGroups = repository.findByTestPartId(testPartId);
        return mapper.toDTOs(questionGroups);
    }

    @Override
    public @NotNull QuestionGroup getFirstOrCreateGroupByTestPartId(Long testPartId) {
        QuestionGroup questionGroup = repository.getFirstGroupByTestPartId(testPartId);
        if (questionGroup != null) {
            return questionGroup;
        }

        TestPart testPart = testPartService.getEntityById(testPartId);
        QuestionGroup newQuestionGroup = QuestionGroup.builder()
                .ordinalNumber(1)
                .isDeleted(false)
                .testPart(testPart)
                .build();
        return repository.save(newQuestionGroup);
    }

    @Override
    @Transactional
    public void swapTwoQuestionGroups(Long groupId1, Long groupId2) {
        QuestionGroup group1 = getEntityById(groupId1);
        QuestionGroup group2 = getEntityById(groupId2);

        Integer tempOrdinalNumber = group1.getOrdinalNumber();
        group1.setOrdinalNumber(group2.getOrdinalNumber());
        group2.setOrdinalNumber(tempOrdinalNumber);

        repository.save(group1);
        repository.save(group2);
    }
}

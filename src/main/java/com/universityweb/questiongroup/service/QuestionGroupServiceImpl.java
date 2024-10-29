package com.universityweb.questiongroup.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionGroupServiceImpl
        extends BaseServiceImpl<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupRepos, QuestionGroupMapper>
        implements QuestionGroupService {

    private TestPartService testPartService;

    @Autowired
    public QuestionGroupServiceImpl(QuestionGroupRepos repository, TestPartService testPartService) {
        super(repository, QuestionGroupMapper.INSTANCE);
        this.testPartService = testPartService;
    }

    @Override
    public QuestionGroupDTO update(Long id, QuestionGroupDTO dto) {
        QuestionGroup existingQuestionGroup = getEntityById(dto.getId());

        existingQuestionGroup.setTitle(dto.getTitle());
        existingQuestionGroup.setOrdinalNumber(dto.getOrdinalNumber());
        existingQuestionGroup.setFrom(dto.getFrom());
        existingQuestionGroup.setTo(dto.getTo());
        existingQuestionGroup.setRequirement(dto.getRequirement());
        existingQuestionGroup.setAudioPath(dto.getAudioPath());
        existingQuestionGroup.setImagePath(dto.getImagePath());
        existingQuestionGroup.setContentToDisplay(dto.getContentToDisplay());
        existingQuestionGroup.setOriginalContent(dto.getOriginalContent());

        QuestionGroup updatedQuestionGroup = repository.save(existingQuestionGroup);
        return mapper.toDTO(updatedQuestionGroup);
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
        throw new RuntimeException("QuestionGroup not found with ID: " + id);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(QuestionGroup entity, QuestionGroupDTO dto) {
        TestPart testPart = testPartService.getEntityById(dto.getTestPartId());
        entity.setTestPart(testPart);
    }

    @Override
    public List<QuestionGroupDTO> getByTestPartId(Long testPartId) {
        Sort sort = Sort.by(Sort.Order.asc("ordinalNumber"));
        List<QuestionGroup> questionGroups = repository.findByTestPartId(testPartId, sort);
        return mapper.toDTOs(questionGroups);
    }
}

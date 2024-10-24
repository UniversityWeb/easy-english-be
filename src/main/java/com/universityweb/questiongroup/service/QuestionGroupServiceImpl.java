package com.universityweb.questiongroup.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionGroupServiceImpl
        extends BaseServiceImpl<QuestionGroup, QuestionGroupDTO, Long, QuestionGroupRepos, QuestionGroupMapper>
        implements QuestionGroupService {

    @Autowired
    public QuestionGroupServiceImpl(QuestionGroupRepos repository) {
        super(repository, QuestionGroupMapper.INSTANCE);
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
}

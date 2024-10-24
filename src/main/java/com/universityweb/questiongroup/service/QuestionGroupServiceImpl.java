package com.universityweb.questiongroup.service;

import com.universityweb.questiongroup.QuestionGroup;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.dto.QuestionGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionGroupServiceImpl implements QuestionGroupService {
    private final QuestionGroupMapper questionGroupMapper = QuestionGroupMapper.INSTANCE;

    @Autowired
    private QuestionGroupRepos questionGroupRepository;

    @Override
    public List<QuestionGroupDTO> getAllQuestionGroups() {
        List<QuestionGroup> questionGroups = questionGroupRepository.findAll();
        return questionGroupMapper.toDTOs(questionGroups);
    }

    @Override
    public QuestionGroupDTO getQuestionGroupById(Long id) {
        QuestionGroup questionGroup = questionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuestionGroup not found with ID: " + id));
        return questionGroupMapper.toDTO(questionGroup);
    }

    @Override
    public QuestionGroupDTO createQuestionGroup(QuestionGroupDTO questionGroupDTO) {
        QuestionGroup questionGroup = questionGroupMapper.toEntity(questionGroupDTO);
        QuestionGroup savedQuestionGroup = questionGroupRepository.save(questionGroup);
        return questionGroupMapper.toDTO(savedQuestionGroup);
    }

    @Override
    public QuestionGroupDTO updateQuestionGroup(QuestionGroupDTO questionGroupDTO) {
        QuestionGroup existingQuestionGroup = getEntityById(questionGroupDTO.getId());

        existingQuestionGroup.setTitle(questionGroupDTO.getTitle());
        existingQuestionGroup.setOrdinalNumber(questionGroupDTO.getOrdinalNumber());
        existingQuestionGroup.setFrom(questionGroupDTO.getFrom());
        existingQuestionGroup.setTo(questionGroupDTO.getTo());
        existingQuestionGroup.setRequirement(questionGroupDTO.getRequirement());
        existingQuestionGroup.setAudioPath(questionGroupDTO.getAudioPath());
        existingQuestionGroup.setImagePath(questionGroupDTO.getImagePath());
        existingQuestionGroup.setContentToDisplay(questionGroupDTO.getContentToDisplay());
        existingQuestionGroup.setOriginalContent(questionGroupDTO.getOriginalContent());
        existingQuestionGroup.setIsDeleted(questionGroupDTO.getIsDeleted());

        QuestionGroup updatedQuestionGroup = questionGroupRepository.save(existingQuestionGroup);
        return questionGroupMapper.toDTO(updatedQuestionGroup);
    }

    @Override
    public void deleteQuestionGroup(Long id) {
        questionGroupRepository.deleteById(id);
    }

    @Override
    public QuestionGroup getEntityById(Long id) {
        return questionGroupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuestionGroup not found with ID: " + id));
    }
}

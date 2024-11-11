package com.universityweb.test.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.questiongroup.QuestionGroupRepos;
import com.universityweb.questiongroup.entity.QuestionGroup;
import com.universityweb.section.service.SectionService;
import com.universityweb.test.TestMapper;
import com.universityweb.test.TestRepos;
import com.universityweb.test.dto.TestDTO;
import com.universityweb.test.entity.Test;
import com.universityweb.test.exception.TestNotFoundException;
import com.universityweb.testpart.TestPartRepos;
import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testpart.service.TestPartService;
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.entity.TestQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestServiceImpl
        extends BaseServiceImpl<Test, TestDTO, Long, TestRepos, TestMapper>
        implements TestService {

    private final SectionService sectionService;
    private final TestPartRepos testPartRepos;
    private final QuestionGroupRepos questionGroupRepos;
    private final TestQuestionRepos testQuestionRepos;

    @Autowired
    public TestServiceImpl(
            TestRepos repository,
            SectionService sectionService,
            TestPartRepos testPartRepos,
            QuestionGroupRepos questionGroupRepos,
            TestQuestionRepos testQuestionRepos
    ) {
        super(repository, TestMapper.INSTANCE);
        this.sectionService = sectionService;
        this.testPartRepos = testPartRepos;
        this.questionGroupRepos = questionGroupRepos;
        this.testQuestionRepos = testQuestionRepos;
    }

    @Override
    public TestDTO getById(Long id) {
        TestDTO testDTO = super.getById(id);
        Long courseId = repository.findCourseIdByTestId(id);
        testDTO.setCourseId(courseId);
        return testDTO;
    }

    @Override
    public void updateStatus(Long id, Test.EStatus status) {
        Test existingTest = getEntityById(id);
        existingTest.setStatus(status);
        repository.save(existingTest);
    }

    @Override
    public List<TestDTO> getBySection(Long sectionId) {
        Sort sort = Sort.by(Sort.Order.asc("ordinalNumber"));
        List<Test> tests = repository.findBySectionId(sectionId, sort);
        return mapper.toDTOs(tests);
    }

    @Override
    @Transactional
    public void refactorOrdinalNumbers(Long testId) {
        Test test = getEntityById(testId);

        List<TestPart> parts = testPartRepos.findByTestOrderByOrdinalNumberAsc(test);

        int partOrdinal = 1;
        int groupOrdinal = 1;
        int questionOrdinal = 1;
        for (TestPart part : parts) {
            part.setOrdinalNumber(partOrdinal++);
            testPartRepos.save(part);

            List<QuestionGroup> groups = questionGroupRepos.findByTestPartOrderByOrdinalNumberAsc(part);

            for (QuestionGroup group : groups) {
                group.setOrdinalNumber(groupOrdinal++);
                group.setFrom(questionOrdinal);

                List<TestQuestion> questions = testQuestionRepos.findByQuestionGroupOrderByOrdinalNumberAsc(group);

                for (TestQuestion question : questions) {
                    question.setOrdinalNumber(questionOrdinal++);
                    testQuestionRepos.save(question);
                }

                group.setTo(questionOrdinal - 1);
                questionGroupRepos.save(group);
            }
        }
    }

    @Override
    public TestDTO update(Long id, TestDTO dto) {
        Test existingTest = getEntityById(dto.getId());

        existingTest.setType(dto.getType());
        existingTest.setStatus(dto.getStatus());
        existingTest.setTitle(dto.getTitle());
        existingTest.setDescription(dto.getDescription());
        existingTest.setOrdinalNumber(dto.getOrdinalNumber());
        existingTest.setDurationInMilis(dto.getDurationInMilis());
        existingTest.setPassingGrade(dto.getPassingGrade());

        return savedAndConvertToDTO(existingTest);
    }

    @Override
    public void softDelete(Long id) {
        super.softDelete(id);

        updateStatus(id, Test.EStatus.DELETED);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        String msg = String.format("Could not find any tests with id=%s", id);
        throw new TestNotFoundException(msg);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Test entity, TestDTO dto) {
        entity.setSection(sectionService.getEntityById(dto.getSectionId()));
    }
}

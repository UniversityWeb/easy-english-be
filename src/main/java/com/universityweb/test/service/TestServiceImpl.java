package com.universityweb.test.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.drip.Drip;
import com.universityweb.drip.DripRepos;
import com.universityweb.drip.dto.PrevDripDTO;
import com.universityweb.lesson.LessonRepository;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lessontracker.LessonTrackerRepository;
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
import com.universityweb.testquestion.TestQuestionRepos;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.entity.TestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl
        extends BaseServiceImpl<Test, TestDTO, Long, TestRepos, TestMapper>
        implements TestService {

    private final SectionService sectionService;
    private final TestPartRepos testPartRepos;
    private final QuestionGroupRepos questionGroupRepos;
    private final TestQuestionRepos testQuestionRepos;
    private final DripRepos dripRepos;
    private final TestResultRepos testResultRepos;
    private final LessonTrackerRepository lessonTrackerRepository;
    private final LessonRepository lessonRepository;
    private final TestRepos testRepos;

    @Autowired
    public TestServiceImpl(
            TestRepos repository,
            TestMapper testMapper,
            SectionService sectionService,
            TestPartRepos testPartRepos,
            QuestionGroupRepos questionGroupRepos,
            TestQuestionRepos testQuestionRepos,
            DripRepos dripRepos,
            TestResultRepos testResultRepos,
            LessonTrackerRepository lessonTrackerRepository,
            LessonRepository lessonRepository,
            TestRepos testRepos
    ) {
        super(repository, testMapper);
        this.sectionService = sectionService;
        this.testPartRepos = testPartRepos;
        this.questionGroupRepos = questionGroupRepos;
        this.testQuestionRepos = testQuestionRepos;
        this.dripRepos = dripRepos;
        this.testResultRepos = testResultRepos;
        this.lessonTrackerRepository = lessonTrackerRepository;
        this.lessonRepository = lessonRepository;
        this.testRepos = testRepos;
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
    @Transactional
    public List<TestDTO> getBySection(String username, Long sectionId) {
        List<Test> tests = repository.findBySectionId(sectionId);

        List<TestDTO> testDTOs = new ArrayList<>();
        for (Test test : tests) {
            Long testId = test.getId();
            List<Drip> drips = dripRepos.findDripByNextId(testId, Drip.ESourceType.TEST);

            boolean isLocked = this.isLocked(username, testId);
            TestDTO testDTO = mapper.toDTOBasedOnIsLocked(isLocked, test);
            refactorOrdinalNumbers(testId);

            testDTO.setPrevDrips(drips.stream()
                    .map(drip -> {
                        Long prevId = drip.getPrevId();
                        String title = "";
                        String type = "";
                        switch (drip.getPrevType()) {
                            case LESSON:
                                Lesson tempLesson = lessonRepository.findById(prevId).orElse(null);
                                if (tempLesson != null) {
                                    title = tempLesson.getTitle() != null ? tempLesson.getTitle() : "Unnamed Lesson";
                                    type = tempLesson.getType() != null ? tempLesson.getType().toString() : "Unknown Lesson Type";
                                }
                                break;

                            case TEST:
                                Test tempTest = testRepos.findById(prevId).orElse(null);
                                if (tempTest != null) {
                                    title = tempTest.getTitle() != null ? tempTest.getTitle() : "Unnamed Test";
                                    type = Drip.ESourceType.TEST.toString();
                                }
                                break;
                        }
                        return new PrevDripDTO(drip.getPrevId(), title, type);
                    })
                    .collect(Collectors.toSet()));

            testDTOs.add(testDTO);
        }

        return testDTOs;
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
    public Boolean isEmptyTest(Long testId) {
        Optional<Test> testOptional = repository.findById(testId);

        if (testOptional.isPresent()) {
            Test test = testOptional.get();

            return test.getParts().stream()
                    .filter(part -> part.getQuestionGroups() != null) // Ensure question groups are not null
                    .flatMap(part -> part.getQuestionGroups().stream())
                    .filter(group -> group.getQuestions() != null) // Ensure questions are not null
                    .mapToLong(group -> group.getQuestions().size())
                    .sum() == 0;
        }

        return true;
    }

    @Override
    public Long getCourseIdByTestId(Long testId) {
        return repository.findCourseIdByTestId(testId);
    }

    @Override
    public TestDTO update(Long id, TestDTO dto) {
        Test existingTest = getEntityById(dto.getId());

        existingTest.setType(dto.getType());
        existingTest.setStatus(dto.getStatus());
        existingTest.setTitle(dto.getTitle());
        existingTest.setDescription(dto.getDescription());
        existingTest.setDurationInMilis(dto.getDurationInMilis());
        existingTest.setPassingGrade(dto.getPassingGrade());

        return savedAndConvertToDTO(existingTest);
    }

    @Override
    public void delete(Long id) {
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

    private boolean isLocked(String username, Long testId) {
        // Step 1: Check if the user has a TestResult for the test
        List<TestResult> testResult = testResultRepos.findByUser_UsernameAndTest_IdOrderByFinishedAtDesc(username, testId);
        if (testResult != null && !testResult.isEmpty()) {
            return false;
        }

        // Step 2: Check for prerequisite lessons or tests using Drip
        List<Drip> drips = dripRepos.findDripByNextId(testId, Drip.ESourceType.TEST);
        for (Drip drip : drips) {
            if (drip.getPrevType() == Drip.ESourceType.LESSON) {
                if (!lessonTrackerRepository.isLessonCompleted(username, drip.getPrevId())) {
                    return true;
                }
            } else if (drip.getPrevType() == Drip.ESourceType.TEST) {
                if (!testResultRepos.isTestDone(username, drip.getPrevId())) {
                    return true;
                }
            }
        }

        // If no locking conditions were met, the test is not locked
        return false;
    }
}

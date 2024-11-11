package com.universityweb.testresult.service;

import com.universityweb.common.Utils;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testquestion.service.TestQuestionService;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.mapper.TestResultMapper;
import com.universityweb.testresult.request.SubmitTestRequest;
import com.universityweb.useranswer.UserAnswerRepos;
import com.universityweb.useranswer.entity.UserAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestResultServiceImpl
        extends BaseServiceImpl<TestResult, TestResultDTO, Long, TestResultRepos, TestResultMapper>
        implements TestResultService {

    private final TestService testService;
    private final UserService userService;
    private final TestQuestionService testQuestionService;
    private final UserAnswerRepos userAnswerRepos;

    @Autowired
    public TestResultServiceImpl(
            TestResultRepos repository,
            TestResultMapper mapper,
            TestService testService,
            UserService userService,
            TestQuestionService testQuestionService,
            UserAnswerRepos userAnswerRepos
    ) {
        super(repository, mapper);
        this.testService = testService;
        this.userService = userService;
        this.testQuestionService = testQuestionService;
        this.userAnswerRepos = userAnswerRepos;
    }

    @Override
    public Page<TestResultDTO> getTestResultsByUsername(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt"));
        Page<TestResult> testResultsPage = repository.findByUser_Username(username, pageable);
        return testResultsPage.map(mapper::toDTO);
    }

    @Override

    public TestResult getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find any test results with id" + id));
    }

    @Override
    public TestResultDTO update(Long aLong, TestResultDTO dto) {
        TestResult testResult = getEntityById(dto.id());

        testResult.setResult(dto.result());
        testResult.setStatus(dto.status());
        testResult.setTakingDuration(dto.takingDuration());
        testResult.setStartedAt(dto.startedAt());
        testResult.setFinishedAt(dto.finishedAt());

        return savedAndConvertToDTO(testResult);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any test results with id=" + id);
    }

    @Override
    public Page<TestResultDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestResult> testResultsPage = repository.findAll(pageable);
        return testResultsPage.map(mapper::toDTO);
    }

    @Override
    public Page<TestResultDTO> getByUsernameAndTestId(String username, Long testId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TestResult> results = repository.findByUser_UsernameAndTest_Id(username, testId, pageable);
        return mapper.mapPageToPageDTO(results);
    }

    @Override
    public List<TestResult> getByUsernameAndTestId(String username, Long testId) {
        return repository.findByUser_UsernameAndTest_Id(username, testId);
    }

    @Override
    public Boolean isDone(String username, Long testId) {
        List<TestResult> testResults = getByUsernameAndTestId(username, testId);
        if (testResults == null || testResults.isEmpty()) return false;
        return testResults.stream()
                .anyMatch(testResult -> testResult.getStatus().equals(TestResult.EStatus.DONE));
    }

    @Override
    @Transactional
    public TestResultDTO submit(String username, SubmitTestRequest req) {
        Long testId = req.getTestId();
        User user = userService.loadUserByUsername(username);
        Test test = testService.getEntityById(testId);

        TestResult testResult = TestResult.builder()
                .result("")
                .status(TestResult.EStatus.FAILED)
                .takingDuration(req.getTakingDuration())
                .startedAt(req.getStartedAt())
                .finishedAt(req.getFinishedAt())
                .user(user)
                .test(test)
                .build();
        TestResult testResultSaved = repository.save(testResult);

        int numberOfQuestions = testQuestionService.getNumberOfQuestions(testId);
        int numberOfCorrectAnswers = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();
        for (SubmitTestRequest.UserAnswerDTO userAnswerDTO : req.getUserAnswers()) {
            Long testQuestionId = userAnswerDTO.getTestQuestionId();
            TestQuestion testQuestion = testQuestionService.getEntityById(testQuestionId);
            Boolean isCorrect = isCorrect(testQuestion, userAnswerDTO.getAnswers());
            UserAnswer userAnswer = UserAnswer.builder()
                    .answers(userAnswerDTO.getAnswers())
                    .isCorrect(isCorrect)
                    .testQuestion(testQuestion)
                    .testResult(testResultSaved)
                    .build();
            userAnswers.add(userAnswer);

            numberOfCorrectAnswers = isCorrect ? numberOfCorrectAnswers + 1 : numberOfCorrectAnswers;
        }

        userAnswerRepos.saveAll(userAnswers);

        double correctPercent = (numberOfCorrectAnswers * 100.0) / numberOfQuestions;
        String result = numberOfCorrectAnswers + "/" + numberOfQuestions;
        Double passingGrade = test.getPassingGrade();
        boolean isPassed = correctPercent >= passingGrade;

        testResultSaved.setResult(result);
        testResultSaved.setStatus(isPassed ? TestResult.EStatus.DONE : TestResult.EStatus.FAILED);
        testResultSaved = repository.save(testResultSaved);

        return mapper.toDTO(testResultSaved);
    }

    private Boolean isCorrect(TestQuestion testQuestion, List<String> userAnswers) {
        return Utils.isEquals(testQuestion.getCorrectAnswers(), userAnswers);
    }
}

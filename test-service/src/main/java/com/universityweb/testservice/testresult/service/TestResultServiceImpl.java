package com.universityweb.testservice.testresult.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.common.util.Utils;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testquestion.entity.TestQuestion;
import com.universityweb.testquestion.service.TestQuestionService;
import com.universityweb.testresult.TestResultRepos;
import com.universityweb.testresult.dto.TestResultDTO;
import com.universityweb.testresult.dto.TestResultWithoutListDTO;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.mapper.TestResultMapper;
import com.universityweb.testresult.request.GetTestResultReq;
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
    private final NotificationService notificationService;

    @Autowired
    public TestResultServiceImpl(
            TestResultRepos repository,
            TestResultMapper mapper,
            TestService testService,
            UserService userService,
            TestQuestionService testQuestionService,
            UserAnswerRepos userAnswerRepos,
            NotificationService notificationService
    ) {
        super(repository, mapper);
        this.testService = testService;
        this.userService = userService;
        this.testQuestionService = testQuestionService;
        this.userAnswerRepos = userAnswerRepos;
        this.notificationService = notificationService;
    }

    @Override
    public TestResultDTO update(Long aLong, TestResultDTO dto) {
        TestResult testResult = getEntityById(dto.getId());

        testResult.setResult(dto.getResult());
        testResult.setCorrectPercent(dto.getCorrectPercent());
        testResult.setStatus(dto.getStatus());
        testResult.setTakingDuration(dto.getTakingDuration());
        testResult.setStartedAt(dto.getStartedAt());
        testResult.setFinishedAt(dto.getFinishedAt());

        return savedAndConvertToDTO(testResult);
    }

    @Override
    public TestResultDTO getById(Long id) {
        TestResult testResult = getEntityById(id);
        return enrichTestResultDTO(testResult);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("Could not find any test results with id=" + id);
    }

    @Override
    public Page<TestResultWithoutListDTO> getTestHistoryByTestId(GetTestResultReq req) {
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
        Page<TestResult> results = repository.findByTest_IdOrderByFinishedAtDesc(req.getTestId(), pageable);
        return results.map(this::enrichTestResultWithoutListDTO);
    }

    @Override
    public List<TestResult> getByUsernameAndTestId(String username, Long testId) {
        return repository.findByUser_UsernameAndTest_IdOrderByFinishedAtDesc(username, testId);
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
        List<SubmitTestRequest.UserAnswerDTO> answerDTOs = req.getUserAnswers();
        if (answerDTOs == null || answerDTOs.isEmpty()) {
            throw new CustomException("Could not find any user answers");
        }

        Long testId = req.getTestId();
        User user = userService.loadUserByUsername(username);
        Test test = testService.getEntityById(testId);

        TestResult savedTestResult = createTestResult(req, user, test);

        int numberOfQuestions = testQuestionService.getNumberOfQuestions(testId);
        int numberOfCorrectAnswers = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();

        List<TestQuestion> testQuestions = testQuestionService.findByTestId(testId);
        for (TestQuestion existingQuestion : testQuestions) {
            SubmitTestRequest.UserAnswerDTO userAnswerDTO = getUserAnswerDTO(answerDTOs, existingQuestion.getId());
            boolean isCorrect = isCorrect(existingQuestion, userAnswerDTO.getAnswers());

            UserAnswer userAnswer = createUserAnswer(userAnswerDTO, isCorrect, existingQuestion, savedTestResult);
            userAnswers.add(userAnswer);
            numberOfCorrectAnswers += isCorrect ? 1 : 0;
        }

        userAnswerRepos.saveAll(userAnswers);
        TestResult savedResult = updateTestResult(savedTestResult, numberOfCorrectAnswers, numberOfQuestions, test);
        sendRealtimeNewResult(savedResult);
        return mapper.toDTO(savedResult);
    }

    @Override
    public Page<TestResultWithoutListDTO> getByCurUser(String username, int page, int size) {
        Sort sort = Sort.by("startedAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TestResult> testResultPage = repository.findAllByUsername(username, pageable);
        return testResultPage.map(this::enrichTestResultWithoutListDTO);
    }

    @Override
    public void softDelete(Long id) {
        TestResult testResult = getEntityById(id);
        testResult.setIsDeleted(true);
        repository.save(testResult);
    }

    private Boolean isCorrect(TestQuestion testQuestion, List<String> userAnswers) {
        return Utils.isEquals(testQuestion.getCorrectAnswers(), userAnswers);
    }

    private TestResult createTestResult(SubmitTestRequest req, User user, Test test) {
        return TestResult.builder()
                .result("")
                .correctPercent(0.0)
                .status(TestResult.EStatus.IN_PROGRESS)
                .takingDuration(req.getTakingDuration())
                .startedAt(req.getStartedAt())
                .finishedAt(req.getFinishedAt())
                .isDeleted(false)
                .user(user)
                .test(test)
                .build();
    }

    private SubmitTestRequest.UserAnswerDTO getUserAnswerDTO(List<SubmitTestRequest.UserAnswerDTO> answerDTOs, Long testQuestionId) {
        return answerDTOs.stream()
                .filter(userAnswer -> userAnswer.getTestQuestionId().equals(testQuestionId))
                .findFirst()
                .orElse(new SubmitTestRequest.UserAnswerDTO(new ArrayList<>(), testQuestionId));
    }

    private UserAnswer createUserAnswer(SubmitTestRequest.UserAnswerDTO userAnswerDTO, boolean isCorrect, TestQuestion existingQuestion, TestResult testResult) {
        return UserAnswer.builder()
                .ordinalNumber(existingQuestion.getOrdinalNumber())
                .answers(userAnswerDTO.getAnswers())
                .isCorrect(isCorrect)
                .testQuestion(existingQuestion)
                .testResult(testResult)
                .build();
    }

    private TestResult updateTestResult(TestResult testResult, int numberOfCorrectAnswers, int numberOfQuestions, Test test) {
        double correctPercent = (numberOfCorrectAnswers * 100.0) / numberOfQuestions;
        String result = numberOfCorrectAnswers + "/" + numberOfQuestions;
        Double passingGrade = test.getPassingGrade();
        boolean isPassed = correctPercent >= passingGrade;

        testResult.setResult(result);
        testResult.setCorrectPercent(correctPercent);
        testResult.setStatus(isPassed ? TestResult.EStatus.DONE : TestResult.EStatus.FAILED);
        testResult = repository.save(testResult);
        return testResult;
    }

    private TestResultWithoutListDTO enrichTestResultWithoutListDTO(TestResult testResult) {
        TestResultWithoutListDTO dtoWithoutList = mapper.toTestResultWithoutListDTO(testResult);
        Long courseId = testService.getCourseIdByTestId(dtoWithoutList.getTestId());
        dtoWithoutList.setCourseId(courseId);
        return dtoWithoutList;
    }

    private TestResultDTO enrichTestResultDTO(TestResult testResult) {
        TestResultDTO testResultDTO = mapper.toDTO(testResult);
        Long courseId = testService.getCourseIdByTestId(testResultDTO.getTestId());
        testResultDTO.setCourseId(courseId);
        return testResultDTO;
    }

    private void sendRealtimeNewResult(TestResult savedResult) {
        TestResultWithoutListDTO testResultWithoutListDTO = mapper.toTestResultWithoutListDTO(savedResult);
        if (testResultWithoutListDTO != null) {
            String destination = WebSocketConstants.testResultNotificationTopic(testResultWithoutListDTO.getTestId());
            notificationService.sendRealtimeNotification(destination, testResultWithoutListDTO);
        }
    }
}

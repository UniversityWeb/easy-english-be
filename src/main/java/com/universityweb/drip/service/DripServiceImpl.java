package com.universityweb.drip.service;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.drip.Drip;
import com.universityweb.drip.DripMapper;
import com.universityweb.drip.DripRepos;
import com.universityweb.drip.dto.DripDTO;
import com.universityweb.drip.dto.DripsOfPrevDTO;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.service.LessonService;
import com.universityweb.lessontracker.service.LessonTrackerService;
import com.universityweb.test.entity.Test;
import com.universityweb.test.service.TestService;
import com.universityweb.testresult.entity.TestResult;
import com.universityweb.testresult.service.TestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DripServiceImpl
        extends BaseServiceImpl<Drip, DripDTO, Long, DripRepos, DripMapper>
        implements DripService {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final TestService testService;
    private final AuthService authService;
    private final LessonTrackerService lessonTrackerService;
    private final TestResultService testResultService;

    @Autowired
    public DripServiceImpl(
            DripRepos repository,
            DripMapper dripMapper,
            CourseService courseService,
            LessonService lessonService,
            TestService testService,
            AuthService authService,
            LessonTrackerService lessonTrackerService,
            TestResultService testResultService
    ) {
        super(repository, dripMapper);
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.testService = testService;
        this.authService = authService;
        this.lessonTrackerService = lessonTrackerService;
        this.testResultService = testResultService;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any drip with id " + id);
    }

    @Override
    protected void checkBeforeAdd(DripDTO dto) {
        Drip.ESourceType sourceType = dto.getPrevType();
        if (sourceType.equals(Drip.ESourceType.LESSON)) {
            Lesson lesson = lessonService.getEntityById(dto.getPrevId());
        } else if (sourceType.equals(Drip.ESourceType.TEST)) {
            Test test = testService.getEntityById(dto.getPrevId());
        }

        Drip.ESourceType targetType = dto.getNextType();
        if (targetType.equals(Drip.ESourceType.LESSON)) {
            Lesson lesson = lessonService.getEntityById(dto.getNextId());
        } else if (targetType.equals(Drip.ESourceType.TEST)) {
            Test test = testService.getEntityById(dto.getNextId());
        }
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Drip entity, DripDTO dto) {
        Course course = courseService.getEntityById(dto.getCourseId());
        entity.setCourse(course);
    }

    @Override
    public DripDTO update(Long id, DripDTO dto) {
        Drip drip = getEntityById(id);

        drip.setPrevId(dto.getId());
        drip.setPrevType(dto.getNextType());
        drip.setNextId(dto.getNextId());
        drip.setNextType(dto.getNextType());

        return savedAndConvertToDTO(drip);
    }

    @Override
    public void softDelete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<DripDTO> getAllDripsByPrevId(Long prevId) {
        List<Drip> drips = repository.findAllByPrevId(prevId);
        List<DripDTO> dripDTOs = mapper.toDTOs(drips);
        return dripDTOs;
    }

    @Override
    public List<DripsOfPrevDTO> getAllDripsByCourseId(Long courseId) {
        List<Drip> drips = repository.findAllByCourseId(courseId);
        List<DripsOfPrevDTO> dripsOfPrevDTOs = new ArrayList<>();

        for (Drip drip : drips) {
            DripsOfPrevDTO dripsOfPrevDTO = createDripOfPrevDTO(drip);
            dripsOfPrevDTOs.add(dripsOfPrevDTO);
        }

        return dripsOfPrevDTOs;
    }

    @Override
    public Boolean canLearn(Drip.ESourceType targetType, Long targetId) {
        String username = authService.getCurrentUsername();

        if (isLearned(username, targetType, targetId)) {
            return true;
        }

        List<Drip> prerequisiteDrips = repository.findAllByNextTypeAndNextId(targetType, targetId);
        if (prerequisiteDrips.isEmpty()) {
            return true;
        }

        // Check if all prerequisites are learned
        for (Drip drip : prerequisiteDrips) {
            if (!isLearned(username, drip.getPrevType(), drip.getPrevId())) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public List<DripsOfPrevDTO> updateDrips(
            Long courseId,
            List<DripsOfPrevDTO> dripsUpdateRequest
    ) {
        repository.deleteByCourseId(courseId);
        Course course = courseService.getEntityById(courseId);

        List<Drip> drips = new ArrayList<>();
        for (DripsOfPrevDTO dripsOfPrevDTO : dripsUpdateRequest) {
            Drip.ESourceType prevType = dripsOfPrevDTO.getPrevType();
            Long prevId = dripsOfPrevDTO.getPrevId();

            dripsOfPrevDTO.getNextDrips().forEach(next -> {
                Drip.ESourceType nextType = next.getNextType();
                Long nextId = next.getNextId();

                Drip drip = Drip.builder()
                        .prevType(prevType)
                        .prevId(prevId)
                        .nextType(nextType)
                        .nextId(nextId)
                        .course(course)
                        .build();
                drips.add(drip);
            });
        }
        repository.saveAll(drips);
        return dripsUpdateRequest;
    }

    private boolean isLearned(String username, Drip.ESourceType targetType, Long targetId) {
        switch (targetType) {
            case LESSON:
                return lessonTrackerService.getByUsernameAndLessonId(username, targetId) != null;
            case TEST:
                List<TestResult> results = testResultService.getByUsernameAndTestId(username, targetId);
                return results != null && !results.isEmpty();
            default:
                return false;
        }
    }

    private DripsOfPrevDTO createDripOfPrevDTO(Drip drip) {
        Long prevId = drip.getPrevId();
        Drip.ESourceType prevType = drip.getPrevType();

        List<Drip> dripsByPrevId = repository.findAllByPrevId(prevId);
        List<DripsOfPrevDTO.DripOfPrevDTO> nextDrips = new ArrayList<>();

        for (Drip dripOfPrev : dripsByPrevId) {
            nextDrips.add(createDripOfPrevDetailDTO(dripOfPrev));
        }

        return DripsOfPrevDTO.builder()
                .id(drip.getId())
                .prevTitle("")
                .prevId(prevId)
                .prevType(prevType)
                .nextDrips(nextDrips)
                .build();
    }

    private DripsOfPrevDTO.DripOfPrevDTO createDripOfPrevDetailDTO(Drip dripOfPrev) {
        Long nextId = dripOfPrev.getNextId();
        Drip.ESourceType nextType = dripOfPrev.getNextType();

        return DripsOfPrevDTO.DripOfPrevDTO.builder()
                .nextId(nextId)
                .nextType(nextType)
                .build();
    }

    private Object getObjectBySourceType(Drip.ESourceType sourceType, Long id) {
        if (sourceType.equals(Drip.ESourceType.LESSON)) {
            return lessonService.getEntityById(id);
        } else if (sourceType.equals(Drip.ESourceType.TEST)) {
            return testService.getEntityById(id);
        } else {
            return null;
        }
    }
}

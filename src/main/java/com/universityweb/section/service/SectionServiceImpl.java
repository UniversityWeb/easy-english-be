package com.universityweb.section.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.order.service.OrderService;
import com.universityweb.section.SectionRepository;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.mapper.SectionMapper;
import com.universityweb.section.request.SectionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.Role;
import java.util.List;

import static com.universityweb.common.auth.entity.User.ERole.*;

@Service
public class SectionServiceImpl
    extends BaseServiceImpl<Section, SectionDTO, Long, SectionRepository, SectionMapper>
    implements SectionService {

    private final CourseService courseService;
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    protected SectionServiceImpl(
            SectionRepository repository,
            SectionMapper mapper,
            CourseService courseService,
            UserService userService,
            OrderService orderService
    ) {
        super(repository, mapper);
        this.courseService = courseService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @Override
    @Transactional
    public SectionDTO createSection(SectionRequest sectionRequest) {
        SectionDTO sectionDTO = mapper.toDTO(sectionRequest);
        return create(sectionDTO);
    }

    @Override
    public SectionDTO updateSection(SectionRequest sectionRequest) {
        Section section = getEntityById(sectionRequest.getId());

        section.setTitle(sectionRequest.getTitle());
        section.setCreatedAt(sectionRequest.getCreatedAt());
        section.setUpdatedAt(sectionRequest.getUpdatedAt());
        return savedAndConvertToDTO(section);
    }

    @Override
    public List<SectionDTO> getAllSectionByCourse(SectionRequest sectionRequest) {
        Long courseId = sectionRequest.getCourseId();
        return getAllSectionByCourse(courseId);
    }

    @Override
    public List<SectionDTO> getAllSectionByCourse(Long courseId) {
        List<Section> sections = getAllSectionEntitiesByCourse(courseId);
        return mapper.toDTOs(sections);
    }

    @Override
    public List<Section> getAllSectionEntitiesByCourse(Long courseId) {
        return repository.findByCourseId(courseId);
    }

    @Override
    public boolean isAccessible(String username, Long sectionId) {
        User.ERole role = userService.loadUserByUsername(username).getRole();

        if (role == User.ERole.ADMIN) {
            return true;
        }

        Section section = getEntityById(sectionId);
        Course course = section.getCourse();

        return switch (role) {
            case STUDENT -> orderService.isPurchasedCourse(username, course.getId());
            case TEACHER -> username.equals(course.getOwner().getUsername());
            default -> false;
        };
    }

    @Override
    protected void throwNotFoundException(Long id) {
        String msg = "Could not find any sections with id=" + id;
        throw new CustomException(msg);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Section entity, SectionDTO dto) {
        Course course = courseService.getEntityById(dto.getCourseId());
        entity.setCourse(course);
    }

    @Override
    public SectionDTO update(Long id, SectionDTO dto) {
        Section section = getEntityById(dto.getId());

        section.setTitle(dto.getTitle());
        section.setCreatedAt(dto.getCreatedAt());
        section.setUpdatedAt(dto.getUpdatedAt());
        return savedAndConvertToDTO(section);
    }

    @Override
    public void softDelete(Long id) {
        Section section = getEntityById(id);
        section.setStatus(Section.EStatus.DELETED);
        repository.save(section);
    }
}

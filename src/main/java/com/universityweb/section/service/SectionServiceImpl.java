package com.universityweb.section.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.section.SectionRepository;
import com.universityweb.section.entity.Section;
import com.universityweb.section.mapper.SectionMapper;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.response.SectionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl
    extends BaseServiceImpl<Section, SectionResponse, Long, SectionRepository, SectionMapper>
    implements SectionService {

    private final CourseRepository courseRepository;

    @Autowired
    protected SectionServiceImpl(SectionRepository repository, CourseRepository courseRepository) {
        super(repository, SectionMapper.INSTANCE);
        this.courseRepository = courseRepository;
    }

    @Override
    public SectionResponse createSection(SectionRequest sectionRequest) {
        Section section = new Section();
        Course course = courseRepository.findById(sectionRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        BeanUtils.copyProperties(sectionRequest, section);
        section.setCourse(course);
        return savedAndConvertToDTO(section);
    }

    @Override
    public SectionResponse updateSection(SectionRequest sectionRequest) {
        Section section = getEntityById(sectionRequest.getId());
        BeanUtils.copyProperties(sectionRequest, section);
        return savedAndConvertToDTO(section);
    }

    @Override
    public void deleteSection(SectionRequest sectionRequest) {
        repository.deleteById(sectionRequest.getId());
    }

    @Override
    public List<SectionResponse> getAllSectionByCourse(SectionRequest sectionRequest) {
        Long courseId = sectionRequest.getCourseId();
        List<Section> sections = repository.findByCourseId(courseId);
        return mapper.toDTOs(sections);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Section not found");
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Section entity, SectionResponse dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        entity.setCourse(course);
    }

    @Override
    public SectionResponse update(Long id, SectionResponse dto) {
        Section section = getEntityById(dto.getId());
        BeanUtils.copyProperties(dto, section);
        return savedAndConvertToDTO(section);
    }
}

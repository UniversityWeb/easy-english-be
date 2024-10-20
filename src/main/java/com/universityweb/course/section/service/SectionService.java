package com.universityweb.course.section.service;

import com.universityweb.course.common.entity.Course;
import com.universityweb.course.section.entity.Section;
import com.universityweb.course.section.request.SectionRequest;
import com.universityweb.course.section.response.SectionResponse;
import com.universityweb.course.common.repository.CourseRepository;
import com.universityweb.course.section.SectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CourseRepository courseRepository;
    public Optional<Section> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    public void createSection(SectionRequest sectionRequest) {
        Section section = new Section();
        Course course = courseRepository.findById(sectionRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        BeanUtils.copyProperties(sectionRequest, section);
        section.setCourse(course);
        sectionRepository.save(section);
    }
    public void updateSection(SectionRequest sectionRequest) {
        Section section = sectionRepository.findById(sectionRequest.getId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        BeanUtils.copyProperties(sectionRequest, section);
        sectionRepository.save(section);
    }

    public void deleteSection(SectionRequest sectionRequest) {
        sectionRepository.deleteById(sectionRequest.getId());
    }
    public List<SectionResponse> getAllSectionByCourse(SectionRequest sectionRequest) {
        Long courseId = sectionRequest.getCourseId();
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        List<SectionResponse> sectionResponses = new ArrayList<>();
        for (Section section : sections) {
            SectionResponse sectionResponse = new SectionResponse();
            BeanUtils.copyProperties(section, sectionResponse);
            sectionResponses.add(sectionResponse);
        }
        return sectionResponses;
    }
}

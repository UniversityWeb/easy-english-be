package com.universityweb.course.service;

import com.universityweb.course.model.Course;
import com.universityweb.course.model.Section;
import com.universityweb.course.model.request.SectionRequest;
import com.universityweb.course.model.response.CourseResponse;
import com.universityweb.course.model.response.SectionResponse;
import com.universityweb.course.repository.SectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CourseService courseService;

    public void deleteSection(int id) {
        sectionRepository.deleteById(id);
    }

    public void newSection(SectionRequest sectionRequest) {
        Section section = new Section();
        Course course = courseService.getCourseById(sectionRequest.getCourseId());
        section.setCourse(course);
        section.setTitle(sectionRequest.getTitle());
        section.setCreatedBy(sectionRequest.getCreatedBy());
        sectionRepository.save(section);
    }

    public void updateSection(Section section) {
        Section currentSection = sectionRepository.findById(section.getId());
        currentSection.setTitle(section.getTitle());
        sectionRepository.save(currentSection);
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section getSectionById(int id) {
        return sectionRepository.findById(id);
    }

    public List<Section> getSectionByCourse(int courseId) {
        return sectionRepository.findByCourseId(courseId);
    }

    public List<SectionResponse> getAllSectionByCourseV1(int courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        List<SectionResponse> sectionResponses = new ArrayList<>();
        for (Section section : sections) {
            SectionResponse sectionResponse = new SectionResponse();
            BeanUtils.copyProperties(section, sectionResponse);
            sectionResponses.add(sectionResponse);
        }
        return sectionResponses;
    }

    public SectionResponse getSectionByIdV1(int id) {
        Section section = sectionRepository.findById(id);
        SectionResponse sectionResponse = new SectionResponse();
        BeanUtils.copyProperties(section, sectionResponse);
        return sectionResponse;
    }
}

package com.universityweb.section.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.course.entity.Course;
import com.universityweb.section.entity.Section;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.response.SectionResponse;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.section.SectionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public interface SectionService extends BaseService<Section, SectionResponse, Long> {
    SectionResponse createSection(SectionRequest sectionRequest);
    SectionResponse updateSection(SectionRequest sectionRequest);
    void deleteSection(SectionRequest sectionRequest);
    List<SectionResponse> getAllSectionByCourse(SectionRequest sectionRequest);
}

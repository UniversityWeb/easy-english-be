package com.universityweb.section.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.entity.Section;
import com.universityweb.section.request.SectionRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SectionService extends BaseService<Section, SectionDTO, Long> {
    SectionDTO createSection(SectionRequest sectionRequest);
    SectionDTO updateSection(SectionRequest sectionRequest);
    List<SectionDTO> getAllSectionByCourse(SectionRequest sectionRequest);
    List<SectionDTO> getAllSectionByCourse(Long courseId);
    List<Section> getAllSectionEntitiesByCourse(Long courseId);
    boolean isAccessible(String username, Long sectionId);
}

package com.universityweb.section;

import com.universityweb.section.dto.SectionDTO;
import com.universityweb.section.request.SectionRequest;
import com.universityweb.section.service.SectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/section")
@RestController
@Tag(name = "Course sections")
public class SectionController {
    @Autowired
    private SectionService sectionService;
    @PostMapping("/create-section")
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionRequest sectionRequest) {
        return  ResponseEntity.ok().body(sectionService.createSection(sectionRequest));
    }

    @PostMapping("/update-section")
    public ResponseEntity<SectionDTO> updateSection(@RequestBody SectionRequest sectionRequest) {
        return  ResponseEntity.ok().body(sectionService.updateSection(sectionRequest));
    }
    @PostMapping("/delete-section")
    public String deleteSection(@RequestBody SectionRequest sectionRequest) {
        sectionService.softDelete(sectionRequest.getId());
        return "Section deleted successfully";
    }

    @PostMapping("/get-all-section-by-course")
    public ResponseEntity<List<SectionDTO>> getAllSectionByCourse(@RequestBody SectionRequest sectionRequest) {
        return ResponseEntity.ok(sectionService.getAllSectionByCourse(sectionRequest));
    }
}

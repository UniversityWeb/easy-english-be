package com.universityweb.course.controller;

import com.universityweb.course.model.Course;
import com.universityweb.course.model.Section;
import com.universityweb.course.model.request.SectionRequest;
import com.universityweb.course.model.response.SectionResponse;
import com.universityweb.course.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/sections")
@RestController
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @GetMapping("")
    public List<Section> getAllSection() {
        return sectionService.getAllSections();
    }

    @PostMapping("")
    public String newSection(@RequestBody SectionRequest sectionRequest) {
        sectionService.newSection(sectionRequest);
        return "Section added successfully";
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getSectionByCourse(@PathVariable int courseId) {
        return ResponseEntity.ok(sectionService.getSectionByCourse(courseId));
    }

    @GetMapping("/getAllSectionByCourse")
    public ResponseEntity<List<SectionResponse>> getAllSectionByCourseV1(@RequestParam int courseId) {
        return ResponseEntity.ok(sectionService.getAllSectionByCourseV1(courseId));
    }

    @GetMapping("/getSectionById")
    public ResponseEntity<SectionResponse> getSectionByIdV1(@RequestParam int id) {
        return ResponseEntity.ok(sectionService.getSectionByIdV1(id));
    }

    @PutMapping("")
    public ResponseEntity<?> updateSection(@RequestBody Section sectionRequest) {
        sectionService.updateSection(sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Section updated successfully");
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteSection(@RequestParam int id) {
        sectionService.deleteSection(id);
        return ResponseEntity.status(HttpStatus.OK).body("Section deleted successfully");
    }

}

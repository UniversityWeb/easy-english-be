package com.universityweb.course.repository;

import com.universityweb.course.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Integer> {
    Section findById(int id);

    List<Section> findByCourseId(int courseId);
}

package com.universityweb.bundle.service;

import com.universityweb.bundle.Bundle;
import com.universityweb.bundle.BundleDTO;
import com.universityweb.bundle.BundleMapper;
import com.universityweb.bundle.BundleRepos;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BundleServiceImpl
        extends BaseServiceImpl<Bundle, BundleDTO, Long, BundleRepos, BundleMapper>
        implements BundleService {

    private final CourseRepository courseRepos;

    @Autowired
    public BundleServiceImpl(
            BundleRepos repository,
            BundleMapper mapper,
            CourseRepository courseRepository
    ) {
        super(repository, mapper);
        this.courseRepos = courseRepository;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find bundle with id " + id);
    }

    @Override
    protected void checkBeforeAdd(BundleDTO dto) {
        super.checkBeforeAdd(dto);
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Bundle entity, BundleDTO dto) {
        super.setEntityRelationshipsBeforeAdd(entity, dto);

        List<Course> courses = courseRepos.findAllById(dto.getCourseIds());
        if (courses.isEmpty()) {
            throw new RuntimeException("No valid courses found for given IDs.");
        }

        entity.getCourses().addAll(courses);
    }

    @Override
    public BundleDTO update(Long bundleId, BundleDTO dto) {
        Bundle bundle = getEntityById(bundleId);
        mapper.updateEntityFromDTO(dto, bundle);

        List<Long> courseIds = dto.getCourseIds();
        if (courseIds != null && !courseIds.isEmpty()) {
            List<Course> courses = courseRepos.findAllById(dto.getCourseIds());
            if (courses.isEmpty()) {
                throw new RuntimeException("No valid courses found for given IDs.");
            }

            bundle.setCourses(courses);
        }

        return savedAndConvertToDTO(bundle);
    }

    @Override
    public void softDelete(Long bundleId) {
        Bundle bundle = getEntityById(bundleId);
        bundle.setIsDeleted(true);
        save(bundle);
    }
}

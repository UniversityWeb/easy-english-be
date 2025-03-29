package com.universityweb.bundle.service;

import com.universityweb.bundle.Bundle;
import com.universityweb.bundle.BundleDTO;
import com.universityweb.bundle.BundleMapper;
import com.universityweb.bundle.BundleRepos;
import com.universityweb.bundle.req.BundleFilterReq;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.auth.AuthServiceImpl;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class BundleServiceImpl
        extends BaseServiceImpl<Bundle, BundleDTO, Long, BundleRepos, BundleMapper>
        implements BundleService {

    private final CourseRepository courseRepos;
    private final AuthService authService;

    @Autowired
    public BundleServiceImpl(
            BundleRepos repository,
            BundleMapper mapper,
            CourseRepository courseRepository,
            AuthService authService) {
        super(repository, mapper);
        this.courseRepos = courseRepository;
        this.authService = authService;
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

        entity.setIsDeleted(false);
        entity.setOwner(authService.getCurUser());
        entity.setCourses(new HashSet<>(courses));
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

            bundle.setCourses(new HashSet<>(courses));
        }

        return savedAndConvertToDTO(bundle);
    }

    @Override
    public void softDelete(Long bundleId) {
        Bundle bundle = getEntityById(bundleId);
        bundle.setIsDeleted(true);
        save(bundle);
    }

    @Override
    public Page<BundleDTO> getMyBundles(BundleFilterReq filterReq) {
        filterReq.setTeacherUsername(authService.getCurrentUsername());
        Pageable pageable = PageRequest.of(filterReq.getPageNumber(), filterReq.getSize());

        Page<Bundle> bundlesPage = repository.findBundlesByFilters(
                filterReq.getTeacherUsername(), filterReq.getName(), pageable
        );

        return mapper.mapPageToPageDTO(bundlesPage);
    }
}

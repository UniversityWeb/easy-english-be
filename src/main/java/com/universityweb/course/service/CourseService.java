package com.universityweb.course.service;

import com.universityweb.course.model.*;
import com.universityweb.course.model.request.CourseRequest;
import com.universityweb.course.model.response.CourseResponse;
import com.universityweb.course.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private TopicRepository topicRepository;

    public Page<CourseResponse> getAllCourseOfTeacher(CourseRequest courseRequest) {
        String createdBy = courseRequest.getCreatedBy();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActiveAndCreatedBy(true,createdBy, pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    public void updateCourse(CourseRequest courseRequest) {
        Course currentCourse = courseRepository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        BeanUtils.copyProperties(courseRequest, currentCourse, "id", "createdAt", "createdBy");

        Level level = levelRepository.findById(courseRequest.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        currentCourse.setLevel(level);
        Topic topic = topicRepository.findById(courseRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        currentCourse.setTopic(topic);
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : courseRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            categories.add(category);
        }
        currentCourse.setCategories(categories);
        courseRepository.save(currentCourse);
    }

    public void createCourse(CourseRequest courseRequest) {
        Course course = new Course();
        Price price = new Price();
        BeanUtils.copyProperties(courseRequest, course, "id", "createdAt");
        price.setPrice(BigDecimal.valueOf(0));
        price.setSalePrice(BigDecimal.valueOf(0));
        course.setPrice(price);
        price.setCourse(course);
        Level level = levelRepository.findById(courseRequest.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        course.setLevel(level);
        Topic topic = topicRepository.findById(courseRequest.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        course.setTopic(topic);
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : courseRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            categories.add(category);
        }
        course.setCategories(categories);
        courseRepository.save(course);
    }

    public void deleteCourse(CourseRequest courseRequest) {
        Course currentCourse = courseRepository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        currentCourse.setIsActive(false);
        courseRepository.save(currentCourse);
    }

    public CourseResponse getMainCourse(CourseRequest courseRequest) {
        Course course = courseRepository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        CourseResponse courseResponse = new CourseResponse();
        BeanUtils.copyProperties(course, courseResponse);
        courseResponse.setTopicId(course.getTopic().getId());
        courseResponse.setLevelId(course.getLevel().getId());
        List<Long> categoryIds = new ArrayList<>();
        for (Category category : course.getCategories()) {
            categoryIds.add(category.getId());
        }
        courseResponse.setCategoryIds(categoryIds);
        return courseResponse;
    }

    public Course getCourseById(Long courseId) {
        String msg = "Could not find any course with id=" + courseId;
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException(msg));
    }
    public Page<CourseResponse> getAllCourseByTopic(CourseRequest courseRequest) {
        Long topicId = courseRequest.getTopicId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActiveAndTopicId(true, topicId, pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    public Page<CourseResponse> getAllCourseByLevel(CourseRequest courseRequest) {
        Long levelId = courseRequest.getLevelId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActiveAndLevelId(true, levelId, pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    public Page<CourseResponse> getAllCourseByListCategory(CourseRequest courseRequest) {
        List<Long> categoryIds = courseRequest.getCategoryIds();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActiveAndCategoriesId(true, categoryIds.get(0), pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    public Page<CourseResponse> getAllCourse(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActive(true,pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> filterCourse(int price, String name) {
        return courseRepository.findAll();
    }

    public List<Course> getTop10Courses() {
        return courseRepository.findAll();
    }
}

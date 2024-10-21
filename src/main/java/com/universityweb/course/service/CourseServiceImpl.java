package com.universityweb.course.service;

import com.universityweb.category.CategoryRepository;
import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.exception.CourseNotFoundException;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.favourite.entity.Favourite;
import com.universityweb.favourite.repository.FavouriteRepository;
import com.universityweb.level.LevelRepository;
import com.universityweb.level.entity.Level;
import com.universityweb.price.entity.Price;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.entity.Review;
import com.universityweb.topic.TopicRepository;
import com.universityweb.topic.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper = CourseMapper.INSTANCE;

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepos enrollmentRepos;
    private final FavouriteRepository favouriteRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Override
    public Page<CourseResponse> getAllCourseOfTeacher(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();
        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActiveAndOwner(true,user, pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = new CourseResponse();
            BeanUtils.copyProperties(course, courseResponse);
            return courseResponse;
        });
    }

    @Override
    public void updateCourse(CourseRequest courseRequest) {
        Course currentCourse = getCourseById(courseRequest.getId());
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

    @Override
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

        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());
        course.setOwner(user);
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(CourseRequest courseRequest) {
        Course currentCourse = getCourseById(courseRequest.getId());
        currentCourse.setIsActive(false);
        courseRepository.save(currentCourse);
    }

    @Override
    public CourseResponse getMainCourse(CourseRequest courseRequest) {
        Course course = courseRepository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return courseMapper.toDTO(course);
    }

    @Override
    public Course getCourseById(Long courseId) {
        String msg = "Could not find any course with id=" + courseId;
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException(msg));
    }
    @Override
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

    @Override
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

    @Override
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

    @Override
    public Page<CourseResponse> getAllCourse(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = courseRepository.findByIsActive(true,pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = courseMapper.toDTO(course);
            List<Review> reviews = reviewRepository.findByCourseId(course.getId());
            courseResponse.setRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
            courseResponse.setRatingCount((long) reviews.size());
            return courseResponse;
        });
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> filterCourse(int price, String name) {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getTop10Courses() {
        return courseRepository.findAll();
    }


    @Override
    public List<CourseResponse> getAllCourseOfStudent(CourseRequest courseRequest) {
        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());
        List<Enrollment> enrollments = enrollmentRepos.findByUser(user);
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            CourseResponse courseResponse = courseMapper.toDTO(course);
            courseResponse.setProgress(enrollment.getProgress());
            courseResponses.add(courseResponse);
        }
        return courseResponses;
    }

    @Override
    public List<CourseResponse> getAllCourseNotOfStudent(CourseRequest courseRequest) {
        // Lấy thông tin User dựa trên username
        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());

        // Lấy danh sách các khóa học mà User đã tham gia
        List<Enrollment> enrollments = enrollmentRepos.findByUser(user);

        // Tạo danh sách các khóa học mà User đã tham gia
        Set<Course> enrolledCourses = enrollments.stream()
                .map(Enrollment::getCourse)
                .collect(Collectors.toSet());

        // Lấy tất cả các khóa học
        List<Course> allCourses = courseRepository.findAll();

        // Lọc những khóa học mà User chưa tham gia
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : allCourses) {
            if (!enrolledCourses.contains(course)) {
                List<Review> reviews = reviewRepository.findByCourseId(course.getId());
                CourseResponse courseResponse = courseMapper.toDTO(course);
                courseResponse.setRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
                courseResponse.setRatingCount((long) reviews.size());
                courseResponses.add(courseResponse);
            }
        }
        return courseResponses;
    }

    @Override
    public List<CourseResponse> getAllCourseFavoriteOfStudent(CourseRequest courseRequest) {
        String username = courseRequest.getOwnerUsername();
        User user = userService.loadUserByUsername(username);
        List<Favourite> favourites = favouriteRepository.findByUser(user);
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Favourite favourite : favourites) {
            Course course = favourite.getCourse();
            List<Review> reviews = reviewRepository.findByCourseId(course.getId());
            CourseResponse courseResponse = courseMapper.toDTO(course);
            courseResponse.setRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
            courseResponse.setRatingCount((long) reviews.size());
            courseResponses.add(courseResponse);
        }
        return courseResponses;
    }

    @Override
    public Course getEntityById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Could not find any courses with id=" + courseId));
    }

    @Override
    public CourseResponse getById(Long courseId) {
        Course course = getEntityById(courseId);
        return courseMapper.toDTO(course);
    }
}

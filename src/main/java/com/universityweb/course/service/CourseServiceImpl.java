package com.universityweb.course.service;

import com.universityweb.category.CategoryRepository;
import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.exception.CourseNotFoundException;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.level.LevelRepository;
import com.universityweb.level.entity.Level;
import com.universityweb.price.entity.Price;
import com.universityweb.review.ReviewRepository;
import com.universityweb.review.entity.Review;
import com.universityweb.topic.TopicRepository;
import com.universityweb.topic.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl
        extends BaseServiceImpl<Course, CourseResponse, Long, CourseRepository, CourseMapper>
        implements CourseService {

    private final CategoryRepository categoryRepository;
    private final LevelRepository levelRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepos enrollmentRepos;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Autowired
    public CourseServiceImpl(
            CourseRepository repository,
            CourseMapper mapper,
            CategoryRepository categoryRepository,
            LevelRepository levelRepository,
            TopicRepository topicRepository,
            EnrollmentRepos enrollmentRepos,
            ReviewRepository reviewRepository,
            UserService userService) {

        super(repository, mapper);
        this.categoryRepository = categoryRepository;
        this.levelRepository = levelRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepos = enrollmentRepos;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    @Override
    public Page<CourseResponse> getAllCourseOfTeacher(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();
        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());

        List<Course.EStatus> excludedStatuses = List.of(Course.EStatus.DELETED);

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByStatusNotInAndOwner(excludedStatuses,user, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Transactional
    @Override
    public CourseResponse updateCourse(CourseRequest req) {
        Course currentCourse = getEntityById(req.getId());
        mapper.updateEntityFromDTO(req, currentCourse);

        Level level = levelRepository.findById(req.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        currentCourse.setLevel(level);
        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        currentCourse.setTopic(topic);
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : req.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            categories.add(category);
        }
        currentCourse.setCategories(categories);
        return savedAndConvertToDTO(currentCourse);
    }

    @Transactional
    @Override
    public CourseResponse createCourse(CourseRequest courseRequest) {
        Course course = mapper.toEntity(courseRequest);

        Price price = new Price();
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
        return savedAndConvertToDTO(course);
    }

    @Transactional
    @Override
    public void deleteCourse(CourseRequest courseRequest) {
        Course currentCourse = getEntityById(courseRequest.getId());
        currentCourse.setStatus(Course.EStatus.DELETED);
        repository.save(currentCourse);
    }

    @Override
    public CourseResponse getMainCourse(CourseRequest courseRequest) {
        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return mapCourseToResponse(course);
    }

    @Override
    public Page<CourseResponse> getAllCourseByTopic(CourseRequest courseRequest) {
        Long topicId = courseRequest.getTopicId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByStatusAndTopicId(Course.EStatus.PUBLISHED, topicId, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourseByLevel(CourseRequest courseRequest) {
        Long levelId = courseRequest.getLevelId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByStatusAndLevelId(Course.EStatus.PUBLISHED, levelId, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourseByListCategory(CourseRequest courseRequest) {
        List<Long> categoryIds = courseRequest.getCategoryIds();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByStatusAndCategoriesId(Course.EStatus.PUBLISHED, categoryIds.get(0), pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourse(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByStatus(Course.EStatus.PUBLISHED,pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = mapper.toDTO(course);
            List<Review> reviews = reviewRepository.findByCourseId(course.getId());
            courseResponse.setRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
            courseResponse.setRatingCount((long) reviews.size());
            return courseResponse;
        });
    }

    @Override
    public List<Course> getAllCourses() {
        return repository.findAll();
    }

    @Override
    public List<Course> filterCourse(int price, String name) {
        return repository.findAll();
    }

    @Override
    public List<Course> getTop10Courses() {
        return repository.findAll();
    }

    @Override
    public List<CourseResponse> getAllCourseOfStudent(CourseRequest courseRequest) {
        User user = userService.loadUserByUsername(courseRequest.getUsername());
        List<Enrollment> enrollments = enrollmentRepos.findByUser(user);
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            CourseResponse courseResponse = mapper.toDTO(course);
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
        List<Course> allCourses = repository.findAll();

        // Lọc những khóa học mà User chưa tham gia
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : allCourses) {
            if (!enrolledCourses.contains(course)) {
                List<Review> reviews = reviewRepository.findByCourseId(course.getId());
                CourseResponse courseResponse = mapper.toDTO(course);
                courseResponse.setRating(reviews.stream().mapToDouble(Review::getRating).average().orElse(0));
                courseResponse.setRatingCount((long) reviews.size());
                courseResponses.add(courseResponse);
            }
        }
        return courseResponses;
    }

    @Override
    public Course getEntityById(Long courseId) {
        return repository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Could not find any courses with id=" + courseId));
    }

    @Override
    public CourseResponse update(Long id, CourseResponse dto) {
        return null;
    }

    @Override
    protected void throwNotFoundException(Long id) {
        String msg = "Could not find any course with id=" + id;
        throw new CourseNotFoundException(msg);
    }

    @Override
    public Page<CourseResponse> getCourseByFilter(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        List<Long> categoryIds = courseRequest.getCategoryIds();
        Long topicId = courseRequest.getTopicId();
        Long levelId = courseRequest.getLevelId();
        BigDecimal price = courseRequest.getPrice();
        Double rating = courseRequest.getRating();
        String title = courseRequest.getTitle();
        List<Course.EStatus> statuses = List.of(Course.EStatus.PUBLISHED);

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findCourseByFilter(categoryIds,topicId,
                levelId,price,rating,title,statuses,pageable);

        return coursePage.map(this::mapCourseToResponse);
    }

    @Override
    public CourseResponse mapCourseToResponse(Course course) {
        CourseResponse courseResponse = mapper.toDTO(course);

        // Fetch reviews and calculate the average rating
        List<Review> reviews = reviewRepository.findByCourseId(course.getId());
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0);

        // Format the rating to one decimal place
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedRating = df.format(averageRating);

        // Set rating and additional course details
        courseResponse.setRating(Double.parseDouble(formattedRating));
        courseResponse.setRatingCount((long) reviews.size());
        courseResponse.setCountStudent(enrollmentRepos.countSalesByCourseId(course.getId()));
        courseResponse.setCountSection((long) course.getSections().size());

        return courseResponse;
    }

    @Transactional
    @Override
    public CourseResponse updateStatus(
            User curUser,
            Long courseId,
            Course.EStatus status
    ) {
        Course course = getEntityById(courseId);
        boolean hasEnrolledStudents = enrollmentRepos.existsByCourseId(courseId);
        if (status == Course.EStatus.DELETED && hasEnrolledStudents) {
            throw new IllegalStateException("Cannot update the course status because it has enrolled students");
        }

        String courseOwnerUsername = course.getOwner().getUsername();
        String currentUsername = curUser.getUsername();
        boolean isAdmin = curUser.getRole().equals(User.ERole.ADMIN);
        boolean isOwner = courseOwnerUsername != null && courseOwnerUsername.equals(currentUsername);
        if (!isOwner && !isAdmin) {
            throw new PermissionDenyException("User is not authorized to update the course status");
        }

        course.setStatus(status);
        return savedAndConvertToDTO(course);
    }

    @Override
    public Page<CourseResponse> getCourseForAdmin(CourseRequest req) {
        int pageNumber = req.getPageNumber();
        int size = req.getSize();

        List<Long> categoryIds = req.getCategoryIds();
        Long topicId = req.getTopicId();
        Long levelId = req.getLevelId();
        BigDecimal price = req.getPrice();
        Double rating = req.getRating();
        String title = req.getTitle();
        String ownerUsername = req.getOwnerUsername();
        Course.EStatus status = req.getStatus();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());

        try {
            Page<Course> coursePage = repository.findCourseForAdmin(categoryIds,topicId,levelId,price,rating,title,ownerUsername,status,pageable);
            return coursePage.map(this::mapCourseToResponse);
        } catch (Exception e) {
            log.error(e);
            return Page.empty();
        }
    }

    @Transactional
    @Override
    public CourseResponse updateCourseAdmin(Long courseId, CourseRequest req) {
        Course currentCourse = getEntityById(req.getId());
        mapper.updateEntityFromDTO(req, currentCourse);

        levelRepository.findById(req.getLevelId()).ifPresent(currentCourse::setLevel);
        topicRepository.findById(req.getTopicId()).ifPresent(currentCourse::setTopic);

        List<Category> categories = new ArrayList<>();
        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            for (Long categoryId : req.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                categories.add(category);
            }
            currentCourse.setCategories(categories);
        }

        return savedAndConvertToDTO(currentCourse);
    }

    @Override
    public void softDelete(Long id) {
        Course course = getEntityById(id);
        course.setStatus(Course.EStatus.DELETED);
        repository.save(course);
    }
}

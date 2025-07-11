package com.universityweb.course.service;

import com.universityweb.category.CategoryRepository;
import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.common.util.FrontendRoutes;
import com.universityweb.course.entity.Course;
import com.universityweb.course.exception.CourseNotFoundException;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.request.GetRelatedCourseReq;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.enrollment.EnrollmentRepos;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.level.LevelRepository;
import com.universityweb.level.entity.Level;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.notification.util.CourseContentNotification;
import com.universityweb.order.entity.Order;
import com.universityweb.order.repository.OrderRepos;
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
import java.time.LocalDateTime;
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
    private final NotificationService notificationService;
    private final OrderRepos orderRepos;

    @Autowired
    public CourseServiceImpl(
            CourseRepository repository,
            CourseMapper mapper,
            CategoryRepository categoryRepository,
            LevelRepository levelRepository,
            TopicRepository topicRepository,
            EnrollmentRepos enrollmentRepos,
            ReviewRepository reviewRepository,
            UserService userService,
            NotificationService notificationService,
            OrderRepos orderRepos
    ) {

        super(repository, mapper);
        this.categoryRepository = categoryRepository;
        this.levelRepository = levelRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepos = enrollmentRepos;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.orderRepos = orderRepos;
    }

    @Override
    public Page<CourseResponse> getAllCourseOfTeacher(String ownerUsername, CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        List<Long> categoryIds = courseRequest.getCategoryIds();
        Long topicId = courseRequest.getTopicId();
        Long levelId = courseRequest.getLevelId();
        BigDecimal price = courseRequest.getPrice();
        Double rating = courseRequest.getRating();
        String title = courseRequest.getTitle();
        Course.EStatus status = courseRequest.getStatus();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findCourseForTeacher(ownerUsername, categoryIds,topicId,
                levelId,price,rating,title,status,pageable);

        return coursePage.map(this::mapCourseToResponse);
    }

    @Transactional
    @Override
    public CourseResponse updateCourse(CourseRequest req) {
        Course currentCourse = getEntityById(req.getId());
        mapper.updateEntityFromDTO(req, currentCourse);

        Level level = levelRepository.findById(req.getLevelId())
                .orElseThrow(() -> new CustomException("Level not found"));
        currentCourse.setLevel(level);
        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new CustomException("Topic not found"));
        currentCourse.setTopic(topic);
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : req.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException("Category not found"));
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
                .orElseThrow(() -> new CustomException("Level not found"));
        course.setLevel(level);

        Topic topic = topicRepository.findById(courseRequest.getTopicId())
                .orElseThrow(() -> new CustomException("Topic not found"));
        course.setTopic(topic);

        List<Category> categories = new ArrayList<>();
        for (Long categoryId : courseRequest.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException("Category not found"));
            categories.add(category);
        }
        course.setCategories(categories);

        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());
        course.setOwner(user);
        return savedAndConvertToDTO(course);
    }

    @Override
    public CourseResponse getMainCourse(CourseRequest courseRequest) {
        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new CustomException("Course not found"));
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

        return coursePage.map(this::mapCourseToResponse);
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
            Course.EStatus status,
            String reason
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

        boolean isPublished = Course.EStatus.PUBLISHED.equals(status);
        boolean isRejected = Course.EStatus.REJECTED.equals(status);
        String courseTitle = course.getTitle();
        if (isAdmin && (isPublished || isRejected)) {
            String notiMsg = isPublished
                    ? CourseContentNotification.courseApproved(courseOwnerUsername, courseTitle)
                    : CourseContentNotification.courseRejected(courseOwnerUsername, courseTitle, reason);
            AddNotificationRequest req = AddNotificationRequest.builder()
                    .previewImage(course.getImagePreview())
                    .message(notiMsg)
                    .url(FrontendRoutes.getCourseDetailRoute(courseId.toString()))
                    .username(courseOwnerUsername)
                    .createdDate(LocalDateTime.now())
                    .build();
            notificationService.sendRealtimeNotification(req);
        }

        if (isOwner && Course.EStatus.PENDING_APPROVAL.equals(status)) {
            String notiMsg = CourseContentNotification.newCoursePendingApproval(courseTitle, courseOwnerUsername);
            AddNotificationRequest req = AddNotificationRequest.builder()
                    .previewImage(course.getImagePreview())
                    .message(notiMsg)
                    .url(FrontendRoutes.getCourseDetailRoute(courseId.toString()))
                    .username(courseOwnerUsername)
                    .createdDate(LocalDateTime.now())
                    .build();
            notificationService.sendRealtimeNotification(req);
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
        try {
            Course currentCourse = getEntityById(req.getId());
            mapper.updateEntityFromDTO(req, currentCourse);

            levelRepository.findById(req.getLevelId()).ifPresent(currentCourse::setLevel);
            topicRepository.findById(req.getTopicId()).ifPresent(currentCourse::setTopic);

            List<Category> categories = new ArrayList<>();
            if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
                for (Long categoryId : req.getCategoryIds()) {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new CustomException("Category not found"));
                    categories.add(category);
                }
                currentCourse.setCategories(categories);
            }

            return savedAndConvertToDTO(currentCourse);
        } catch (Exception e) {
            log.error(e);
            throw new CustomException("Failed to update course" + e.getMessage());
        }
    }

    @Override
    public List<CourseResponse> getRelatedCourses(GetRelatedCourseReq req) {
        Long courseId = req.getCourseId();
        int numberOfCourses = req.getNumberOfCourses();
        Pageable pageable = PageRequest.of(0, numberOfCourses);

        List<Course> courses = switch (req.getType()) {
            case LEVEL -> levelRepository.getRelatedCoursesByLevel(courseId, pageable);
            case TOPIC -> topicRepository.getRelatedCoursesByTopic(courseId, pageable);
            default -> new ArrayList<>();
        };

        return courses.stream()
                .map(this::mapCourseToResponse)
                .toList();
    }

    @Transactional
    @Override
    public CourseResponse updateNotice(CourseRequest req) {
        try {
            Course currentCourse = getEntityById(req.getId());
            currentCourse.setNotice(req.getNotice());
            return savedAndConvertToDTO(currentCourse);
        } catch (Exception e) {
            log.error(e);
            throw new CustomException("Failed to update course notice");
        }
    }

    @Transactional
    @Override
    public void incrementViewCount(Long courseId) {
        getEntityById(courseId);
        repository.incrementViewCount(courseId);
    }

    @Override
    public boolean isAccessible(String username, Long courseId) {
        User.ERole role = userService.loadUserByUsername(username).getRole();

        if (role == User.ERole.ADMIN) {
            return true;
        }

        Course course = getEntityById(courseId);

        return switch (role) {
            case STUDENT -> hasStudentPurchasedCourse(username, courseId);
            case TEACHER -> isTeacherOwnerOfCourse(username, course);
            default -> false;
        };
    }

    @Override
    public boolean canEdit(String username, Long courseId) {
        return canDelete(username, courseId);
    }

    @Override
    public boolean canDelete(String username, Long courseId) {
        User.ERole role = userService.loadUserByUsername(username).getRole();

        if (role == User.ERole.ADMIN) {
            return true;
        }

        Course course = getEntityById(courseId);

        return role.equals(User.ERole.TEACHER) && isTeacherOwnerOfCourse(username, course);
    }

    @Override
    public void delete(Long id) {
        Course course = getEntityById(id);
        course.setStatus(Course.EStatus.DELETED);
        repository.save(course);
    }

    private boolean hasStudentPurchasedCourse(String username, Long courseId) {
        return orderRepos.findByUserUsernameAndStatus(username, Order.EStatus.PAID).stream()
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getCourse().getId().equals(courseId));
    }

    private boolean isTeacherOwnerOfCourse(String username, Course course) {
        return username.equals(course.getOwner().getUsername());
    }
}

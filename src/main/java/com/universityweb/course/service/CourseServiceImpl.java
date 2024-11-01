package com.universityweb.course.service;

import com.universityweb.category.CategoryRepository;
import com.universityweb.category.entity.Category;
import com.universityweb.common.auth.entity.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    private final FavouriteRepository favouriteRepository;
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
            FavouriteRepository favouriteRepository,
            ReviewRepository reviewRepository,
            UserService userService) {

        super(repository, mapper);
        this.categoryRepository = categoryRepository;
        this.levelRepository = levelRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepos = enrollmentRepos;
        this.favouriteRepository = favouriteRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }


    @Override
    public Page<CourseResponse> getAllCourseOfTeacher(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();
        User user = userService.loadUserByUsername(courseRequest.getOwnerUsername());

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByIsActiveAndOwner(true,user, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public void updateCourse(CourseRequest courseRequest) {
        Course currentCourse = getEntityById(courseRequest.getId());
        BeanUtils.copyProperties(courseRequest, currentCourse, "id", "createdAt");

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
        currentCourse.setIsActive(true);
        repository.save(currentCourse);
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
        course.setIsActive(true);
        repository.save(course);
    }

    @Override
    public void deleteCourse(CourseRequest courseRequest) {
        Course currentCourse = getEntityById(courseRequest.getId());
        currentCourse.setIsActive(false);
        repository.save(currentCourse);
    }

    @Override
    public CourseResponse getMainCourse(CourseRequest courseRequest) {
        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));


        CourseResponse courseResponse = mapper.toDTO(course);
        List<Review> reviews = reviewRepository.findByCourseId(course.getId());
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0);

        // Định dạng số với một chữ số thập phân
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedRating = df.format(averageRating);

        courseResponse.setRating(Double.parseDouble(formattedRating));
        courseResponse.setRatingCount((long) reviews.size());
        courseResponse.setCountStudent(enrollmentRepos.countSalesByCourseId(course.getId()));
        courseResponse.setCountSection((long) course.getSections().size());
        return courseResponse;
    }

    @Override
    public Page<CourseResponse> getAllCourseByTopic(CourseRequest courseRequest) {
        Long topicId = courseRequest.getTopicId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByIsActiveAndTopicId(true, topicId, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourseByLevel(CourseRequest courseRequest) {
        Long levelId = courseRequest.getLevelId();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByIsActiveAndLevelId(true, levelId, pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourseByListCategory(CourseRequest courseRequest) {
        List<Long> categoryIds = courseRequest.getCategoryIds();
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByIsActiveAndCategoriesId(true, categoryIds.get(0), pageable);

        return coursePage.map(mapper::toDTO);
    }

    @Override
    public Page<CourseResponse> getAllCourse(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findByIsActive(true,pageable);

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
    public List<CourseResponse> getAllCourseFavoriteOfStudent(CourseRequest courseRequest) {
        String username = courseRequest.getUsername();
        User user = userService.loadUserByUsername(username);
        List<Favourite> favourites = favouriteRepository.findByUser(user);
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Favourite favourite : favourites) {
            Course course = favourite.getCourse();
            CourseResponse courseResponse = mapper.toDTO(course);
            List<Review> reviews = reviewRepository.findByCourseId(course.getId());
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);

// Định dạng số với một chữ số thập phân
            DecimalFormat df = new DecimalFormat("#.#");
            String formattedRating = df.format(averageRating);

            courseResponse.setRating(Double.parseDouble(formattedRating));
            courseResponse.setRatingCount((long) reviews.size());
            courseResponse.setCountStudent(enrollmentRepos.countSalesByCourseId(course.getId()));
            courseResponse.setCountSection((long) course.getSections().size());
            courseResponses.add(courseResponse);
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
    public CourseResponse getById(Long courseId) {
        Course course = getEntityById(courseId);
        return mapper.toDTO(course);
    }

    @Override
    public void addCourseToFavorite(CourseRequest courseRequest) {
        User user = userService.loadUserByUsername(courseRequest.getUsername());
        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Favourite favourite = Favourite.builder()
                .user(user)
                .course(course)
                .build();
        favouriteRepository.save(favourite);
    }

    @Override
    public void removeCourseFromFavorite(CourseRequest courseRequest) {
        User user = userService.loadUserByUsername(courseRequest.getUsername());
        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Favourite favourite = favouriteRepository.findByUserAndCourse(user, course);
        favouriteRepository.delete(favourite);
    }

    @Override
    public Boolean checkCourseInFavorite(CourseRequest courseRequest) {
        User user = userService.loadUserByUsername(courseRequest.getUsername());

        Course course = repository.findById(courseRequest.getId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Favourite favourite = favouriteRepository.findByUserAndCourse(user, course);
        return favourite != null;
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

        Sort sort = Sort.by("createdAt");
        Pageable pageable = PageRequest.of(pageNumber, size, sort.descending());
        Page<Course> coursePage = repository.findCourseByFilter(categoryIds,topicId,levelId,price,rating,title,pageable);

        return coursePage.map(course -> {
            CourseResponse courseResponse = mapper.toDTO(course);
            List<Review> reviews = reviewRepository.findByCourseId(course.getId());
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);

            // Định dạng số với một chữ số thập phân
            DecimalFormat df = new DecimalFormat("#.#");
            String formattedRating = df.format(averageRating);

            courseResponse.setRating(Double.parseDouble(formattedRating));
            courseResponse.setRatingCount((long) reviews.size());
            courseResponse.setCountStudent(enrollmentRepos.countSalesByCourseId(course.getId()));
            courseResponse.setCountSection((long) course.getSections().size());
            return courseResponse;
        });
    }
}

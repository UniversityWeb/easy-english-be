package com.universityweb.favourite.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.course.entity.Course;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.favourite.FavouriteMapper;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import com.universityweb.favourite.repository.FavouriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class FavouriteServiceImpl
        extends BaseServiceImpl<Favourite, FavouriteDTO, Long, FavouriteRepository, FavouriteMapper>
        implements FavouriteService {

    private final UserService userService;
    private final CourseService courseService;

    @Autowired
    public FavouriteServiceImpl(
            FavouriteRepository repository,
            FavouriteMapper mapper,
            UserService userService,
            CourseService courseService
    ) {
        super(repository, mapper);
        this.userService = userService;
        this.courseService = courseService;
    }

    @Override
    protected void setEntityRelationshipsBeforeAdd(Favourite entity, FavouriteDTO dto) {
        String username = dto.getUsername();
        Long courseId = dto.getCourseId();
        User user = userService.loadUserByUsername(username);
        Course course = courseService.getEntityById(courseId);

        entity.setUser(user);
        entity.setCourse(course);
        entity.setIsDeleted(false);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find Favourite with ID " + id);
    }

    @Override
    public FavouriteDTO update(Long id, FavouriteDTO dto) {
        return null;
    }

    @Override
    public void softDelete(Long id) {
        Favourite favourite = getEntityById(id);
        favourite.setIsDeleted(true);
        repository.save(favourite);
    }

    @Override
    public Page<CourseResponse> getAllFavorites(String username, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get paginated list of favorites
        Page<Favourite> favouritesPage = repository.findByUser_UsernameAndIsDeletedFalse(username, pageable);

        // Map favorites to CourseResponse
        return favouritesPage.map(favourite -> courseService.mapCourseToResponse(favourite.getCourse()));
    }

    @Override
    public Page<CourseResponse> getAllFavoritesByFilter(CourseRequest courseRequest) {
        int pageNumber = courseRequest.getPageNumber();
        int size = courseRequest.getSize();

        List<Long> categoryIds = courseRequest.getCategoryIds();
        Long topicId = courseRequest.getTopicId();
        Long levelId = courseRequest.getLevelId();
        BigDecimal price = courseRequest.getPrice();
        Double rating = courseRequest.getRating();
        String title = courseRequest.getTitle();
        String username = courseRequest.getUsername();

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        // Fetch filtered favorites using custom query method
        Page<Favourite> favoritePage = repository.findByUserAndFilter(
                username, categoryIds, topicId, levelId, price, rating, title, pageable);

        // Map favorites to CourseResponse
        return favoritePage.map(favourite -> courseService.mapCourseToResponse(favourite.getCourse()));
    }

    @Override
    public Boolean checkCourseInFavorite(String username, Long courseId) {
        Optional<Favourite> optionalFavourite = repository.findByUser_UsernameAndCourse_IdAndIsDeletedFalse(username, courseId);
        return optionalFavourite.isPresent();
    }
}

package com.universityweb.userservice.favourite.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.course.request.CourseRequest;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import org.springframework.data.domain.Page;

public interface FavouriteService extends BaseService<Favourite, FavouriteDTO, Long> {
    Page<CourseResponse> getAllFavorites(String username, int page, int size);
    Page<CourseResponse> getAllFavoritesByFilter(CourseRequest courseRequest);
    Boolean checkCourseInFavorite(String username, Long courseId);

    Favourite getByUsernameAndCourseId(String username, Long courseId);
}

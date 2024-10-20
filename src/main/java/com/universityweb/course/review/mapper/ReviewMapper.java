package com.universityweb.course.review.mapper;

import com.universityweb.course.review.entity.Review;
import com.universityweb.course.review.response.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "user.username", target = "owner")
    ReviewResponse toDTO(Review entity);
    List<ReviewResponse> toDTOs(List<Review> entities);

    @Mapping(target = "user", ignore = true)
    Review toEntity(ReviewResponse dto);
    List<Review> toEntities(List<ReviewResponse> dtos);
}

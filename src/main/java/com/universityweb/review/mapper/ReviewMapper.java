package com.universityweb.review.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.review.dto.ReviewDTO;
import com.universityweb.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper extends BaseMapper<Review, ReviewDTO> {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "user.username", target = "owner")
    @Mapping(source = "parentReview.id", target = "parentReviewId")
    ReviewDTO toDTO(Review entity);
    List<ReviewDTO> toDTOs(List<Review> entities);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentReview", ignore = true)
    @Mapping(target = "childReviews", ignore = true)
    Review toEntity(ReviewDTO dto);
    List<Review> toEntities(List<ReviewDTO> dtos);
}

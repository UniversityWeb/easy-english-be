package com.universityweb.review.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.review.entity.Review;
import com.universityweb.review.response.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper extends BaseMapper<Review, ReviewResponse> {
    @Mapping(source = "userId", target = "owner")
    ReviewResponse toDTO(Review entity);

    @Mapping(target = "courseId", ignore = true)
    Review toEntity(ReviewResponse dto);
}

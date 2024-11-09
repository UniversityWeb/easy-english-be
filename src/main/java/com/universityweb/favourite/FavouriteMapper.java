package com.universityweb.favourite;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavouriteMapper extends BaseMapper<Favourite, FavouriteDTO> {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "course.id", target = "courseId")
    @Override
    FavouriteDTO toDTO(Favourite entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Override
    Favourite toEntity(FavouriteDTO dto);
}

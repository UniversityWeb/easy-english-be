package com.universityweb.favourite;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.favourite.dto.FavouriteDTO;
import com.universityweb.favourite.entity.Favourite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavouriteMapper extends BaseMapper<Favourite, FavouriteDTO> {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "courseId", target = "courseId")
    @Override
    FavouriteDTO toDTO(Favourite entity);

    @Mapping(target = "isDeleted", ignore = true)
    @Override
    Favourite toEntity(FavouriteDTO dto);
}

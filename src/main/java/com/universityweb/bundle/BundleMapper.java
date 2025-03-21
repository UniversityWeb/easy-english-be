package com.universityweb.bundle;

import com.universityweb.common.infrastructure.BaseMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BundleMapper extends BaseMapper<Bundle, BundleDTO> {

    @Mapping(target = "courseIds", ignore = true)
    @Override
    BundleDTO toDTO(Bundle entity);

    @Mapping(target = "courses", ignore = true)
    @Override
    Bundle toEntity(BundleDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // Ignore null values
    void updateEntityFromDTO(BundleDTO dto, @MappingTarget Bundle entity);
}

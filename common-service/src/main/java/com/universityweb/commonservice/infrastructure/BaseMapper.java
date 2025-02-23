package com.universityweb.commonservice.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface BaseMapper<E, D> {
    D toDTO(E entity);
    E toEntity(D dto);
    List<D> toDTOs(List<E> entities);
    List<E> toEntities(List<D> dtos);

    default Page<D> mapPageToPageDTO(Page<E> page) {
        List<D> dtos = toDTOs(page.getContent());
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }
}

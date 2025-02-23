package com.universityweb.commonservice.infrastructure.service;

import java.util.List;

public interface BaseService<E, D, ID> {
    List<D> getAll();
    D getById(ID id);
    E getEntityById(ID id);
    D create(D dto);
    D update(ID id, D dto);
    void softDelete(ID id);
    E save(E entity);
    D savedAndConvertToDTO(E entity);
}

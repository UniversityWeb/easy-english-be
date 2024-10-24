package com.universityweb.common.infrastructure.service;

import com.universityweb.common.infrastructure.BaseMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public abstract class BaseServiceImpl<E, D, ID, REPOS extends JpaRepository<E, ID>, MAPPER extends BaseMapper<E, D>>
        implements BaseService<E, D, ID> {

    protected final REPOS repository;
    protected final MAPPER mapper;

    protected BaseServiceImpl(REPOS repository, MAPPER mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<D> getAll() {
        return mapper.toDTOs(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public D getById(ID id) {
        E entity = getEntityById(id);
        if (entity == null) {
            throwNotFoundException(id);
        }
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public E getEntityById(ID id) {
        return repository.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public D create(D dto) {
        E entity = mapper.toEntity(dto);
        E savedEntity = repository.save(entity);
        return mapper.toDTO(savedEntity);
    }

    protected abstract void throwNotFoundException(ID id);
}

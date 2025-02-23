package com.universityweb.commonservice.infrastructure.service;

import com.universityweb.common.infrastructure.BaseMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public abstract class BaseServiceImpl<E, D, ID, REPOS extends JpaRepository<E, ID>, MAPPER extends BaseMapper<E, D>>
        implements BaseService<E, D, ID> {

    protected final Logger log;

    protected final REPOS repository;
    protected final MAPPER mapper;

    protected BaseServiceImpl(REPOS repository, MAPPER mapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.log = LogManager.getLogger(getClass());
    }

    @Override
    public List<D> getAll() {
        return mapper.toDTOs(repository.findAll());
    }

    @Override
    public D getById(ID id) {
        E entity = getEntityById(id);
        return mapper.toDTO(entity);
    }

    @Override
    public E getEntityById(ID id) {
        Optional<E> entityOpt = repository.findById(id);
        if (entityOpt.isEmpty()) {
            throwNotFoundException(id);
            return null;
        }
        return entityOpt.get();
    }

    @Transactional
    @Override
    public D create(D dto) {
        checkBeforeAdd(dto);
        E entity = mapper.toEntity(dto);
        this.setEntityRelationshipsBeforeAdd(entity, dto);
        E savedEntity = repository.save(entity);
        return mapper.toDTO(savedEntity);
    }

    @Override
    public D savedAndConvertToDTO(E entity) {
        try {
            E saved = repository.save(entity);
            return mapper.toDTO(saved);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Transactional
    @Override
    public void softDelete(ID id) {
    }

    @Transactional
    @Override
    public E save(E entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
            log.error("Failed to save: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    @Override
    public D update(ID id, D dto) {
        return null;
    }

    protected abstract void throwNotFoundException(ID id);

    protected void setEntityRelationshipsBeforeAdd(E entity, D dto) {
    }

    protected void checkBeforeAdd(D dto) {
    }
}

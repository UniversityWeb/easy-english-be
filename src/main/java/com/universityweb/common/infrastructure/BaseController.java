package com.universityweb.common.infrastructure;

import com.universityweb.common.infrastructure.service.BaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BaseController<E, D, ID, SERVICE extends BaseService<E, D, ID>> {

    protected final Logger log;
    protected final SERVICE service;

    private final String entityName;

    protected BaseController(SERVICE service) {
        this.service = service;
        this.log = LogManager.getLogger(getClass());
        this.entityName = inferEntityNameFromGenericType();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<D>> getAll() {
        log.info("Fetching all {}s", entityName);
        List<D> dtos = service.getAll();
        log.info("Fetched {} {}s", dtos.size(), entityName);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<D> getById(@PathVariable ID id) {
        log.info("Fetching {} with ID: {}", entityName, id);
        D dto = service.getById(id);
        log.info("Found {} with ID: {}", entityName, id);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<D> create(@RequestBody D dto) {
        log.info("Creating new {}", entityName);
        D createdDto = service.create(dto);
        log.info("Created new {} with ID: {}", entityName, createdDto);
        return ResponseEntity.status(201).body(createdDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<D> update(
            @PathVariable ID id,
            @RequestBody D dto
    ) {
        log.info("Updating {} with ID: {}", entityName, id);
        D updatedDto = service.update(id, dto);
        log.info("Updated {} with ID: {}", entityName, id);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        log.info("Deleting {} with ID: {}", entityName, id);
        service.softDelete(id);
        log.info("Deleted {} with ID: {}", entityName, id);
        return ResponseEntity.noContent().build();
    }

    private String inferEntityNameFromGenericType() {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<?> entityClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];  // E is at index 0

        return entityClass.getSimpleName().toLowerCase();
    }
}

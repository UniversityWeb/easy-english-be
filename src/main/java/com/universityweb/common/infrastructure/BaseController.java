package com.universityweb.common.infrastructure;

import com.universityweb.common.infrastructure.service.BaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<E, D, ID, SERVICE extends BaseService<E, D, ID>> {

    protected final Logger log;
    protected final SERVICE service;

    protected BaseController(SERVICE service) {
        this.service = service;
        this.log = LogManager.getLogger(getClass());
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<D>> getAll() {
        log.info("Fetching all entities");
        List<D> dtos = service.getAll();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<D> getById(@PathVariable ID id) {
        log.info("Fetching entity with ID: {}", id);
        D dto = service.getById(id);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/add")
    public ResponseEntity<D> create(@RequestBody D dto) {
        log.info("Creating new entity: {}", dto);
        D createdDto = service.create(dto);
        return ResponseEntity.status(201).body(createdDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<D> update(
            @PathVariable ID id,
            @RequestBody D dto
    ) {
        log.info("Updating entity with ID: {}", id);
        D updatedDto = service.update(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}

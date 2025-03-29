package com.universityweb.bundle;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepos extends JpaRepository<Bundle, Long> {

    @Query("""
        SELECT b FROM Bundle b WHERE
        (:teacherUsername IS NULL OR b.owner.username = :teacherUsername) AND
        (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')))
    """)
    Page<Bundle> findBundlesByFilters(
            @Param("teacherUsername") String teacherUsername,
            @Param("name") String name,
            Pageable pageable
    );
}

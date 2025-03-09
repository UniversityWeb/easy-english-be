package com.universityweb.bundle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepos extends JpaRepository<Bundle, Long> {
}

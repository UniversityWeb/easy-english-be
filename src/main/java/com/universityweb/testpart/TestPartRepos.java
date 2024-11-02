package com.universityweb.testpart;

import com.universityweb.testpart.entity.TestPart;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestPartRepos extends JpaRepository<TestPart, Long> {
    @Query("SELECT tp FROM TestPart tp WHERE tp.test.id = :testId AND tp.isDeleted = false")
    List<TestPart> findByTestId(Long testId, Sort sort);

    @Query("SELECT tp FROM TestPart tp WHERE tp.id = :id AND tp.isDeleted = false")
    Optional<TestPart> findByIdAndNotDeleted(Long id);

    @Query("SELECT tp FROM TestPart tp " +
            "WHERE tp.test.id = :testId ORDER BY tp.ordinalNumber ASC")
    TestPart getFirstTestPartByTestId(Long testId);
}

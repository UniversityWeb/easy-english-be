package com.universityweb.engagementservice.faq;

import com.universityweb.faq.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByCourseId(Long courseId);
}

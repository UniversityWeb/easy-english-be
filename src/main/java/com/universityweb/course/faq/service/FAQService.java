package com.universityweb.course.faq.service;

import com.universityweb.course.common.entity.Course;
import com.universityweb.course.faq.entity.FAQ;
import com.universityweb.course.faq.request.FAQRequest;
import com.universityweb.course.faq.response.FAQResponse;
import com.universityweb.course.common.repository.CourseRepository;
import com.universityweb.course.faq.FAQRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FAQService {
    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private CourseRepository courseRepository;


    public void createFAQ(FAQRequest faqRequest) {
        FAQ faq = new FAQ();
        Course course = courseRepository.findById(faqRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        BeanUtils.copyProperties(faqRequest, faq);
        faq.setCourse(course);
        faqRepository.save(faq);
    }
    public void updateFAQ(FAQRequest faqRequest) {
        FAQ faq = faqRepository.findById(faqRequest.getId())
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        BeanUtils.copyProperties(faqRequest, faq);
        faqRepository.save(faq);
    }


    public void deleteFAQ(FAQRequest faqRequest) {
        faqRepository.deleteById(faqRequest.getId());
    }
    public List<FAQResponse> getAllFAQByCourse(FAQRequest faqRequest) {
        Long courseId = faqRequest.getCourseId();
        List<FAQ> faqs = faqRepository.findByCourseId(courseId);
        List<FAQResponse> faqResponses = new ArrayList<>();
        for (FAQ faq : faqs) {
            FAQResponse faqResponse = new FAQResponse();
            BeanUtils.copyProperties(faq, faqResponse);
            faqResponses.add(faqResponse);
        }
        return faqResponses;
    }
}

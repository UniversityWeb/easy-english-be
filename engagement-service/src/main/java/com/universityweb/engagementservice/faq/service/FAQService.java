package com.universityweb.engagementservice.faq.service;

import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.faq.FAQRepository;
import com.universityweb.faq.entity.FAQ;
import com.universityweb.faq.request.FAQRequest;
import com.universityweb.faq.response.FAQResponse;
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


    public FAQResponse createFAQ(FAQRequest faqRequest) {
        FAQ faq = new FAQ();
        Course course = courseRepository.findById(faqRequest.getCourseId())
                .orElseThrow(() -> new CustomException("Course not found"));
        BeanUtils.copyProperties(faqRequest, faq);
        faq.setCourse(course);

        FAQResponse faqResponse = new FAQResponse();
        BeanUtils.copyProperties(faqRepository.save(faq), faqResponse);
        faqResponse.setCourseId(faq.getCourse().getId());
        return faqResponse;
    }
    public FAQResponse updateFAQ(FAQRequest faqRequest) {
        FAQ faq = faqRepository.findById(faqRequest.getId())
                .orElseThrow(() -> new CustomException("FAQ not found"));
        BeanUtils.copyProperties(faqRequest, faq);
        FAQResponse faqResponse = new FAQResponse();
        BeanUtils.copyProperties(faqRepository.save(faq), faqResponse);
        faqResponse.setCourseId(faq.getCourse().getId());
        return faqResponse;
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

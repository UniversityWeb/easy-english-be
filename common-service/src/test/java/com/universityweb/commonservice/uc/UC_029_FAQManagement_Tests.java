package com.universityweb.commonservice.uc;

import com.universityweb.common.exception.CustomException;
import com.universityweb.course.entity.Course;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.faq.FAQRepository;
import com.universityweb.faq.entity.FAQ;
import com.universityweb.faq.request.FAQRequest;
import com.universityweb.faq.response.FAQResponse;
import com.universityweb.faq.service.FAQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_029_FAQManagement_Tests {

    @InjectMocks
    private FAQService faqService;

    @Mock
    private FAQRepository faqRepository;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFAQ_Success() {
        // Arrange
        FAQRequest faqRequest = new FAQRequest(null, 1L, "What is this course about?", "This course is about Java.");
        Course course = Course.builder().id(1L).build();
        FAQ faq = FAQ.builder()
                .id(1L)
                .question("What is this course about?")
                .answer("This course is about Java.")
                .course(course)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(faqRepository.save(any(FAQ.class))).thenReturn(faq);

        // Act
        FAQResponse result = faqService.createFAQ(faqRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals("What is this course about?", result.getQuestion());
        verify(faqRepository, times(1)).save(any(FAQ.class));
    }


    @Test
    void testUpdateFAQ_Success() {
        // Arrange
        FAQRequest faqRequest = new FAQRequest(1L, null, "Updated question?", "Updated answer.");
        FAQ existingFAQ = FAQ.builder()
                .id(1L)
                .question("Old question?")
                .answer("Old answer.")
                .course(Course.builder().id(1L).build())
                .build();

        when(faqRepository.findById(1L)).thenReturn(Optional.of(existingFAQ));
        when(faqRepository.save(any(FAQ.class))).thenReturn(existingFAQ);

        // Act
        FAQResponse result = faqService.updateFAQ(faqRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated question?", result.getQuestion());
        assertEquals("Updated answer.", result.getAnswer());
        verify(faqRepository, times(1)).save(existingFAQ);
    }



    @Test
    void testDeleteFAQ_Success() {
        // Arrange
        FAQRequest faqRequest = new FAQRequest(1L, null, null, null);
        doNothing().when(faqRepository).deleteById(1L);

        // Act
        faqService.deleteFAQ(faqRequest);

        // Assert
        verify(faqRepository, times(1)).deleteById(1L);
    }

}

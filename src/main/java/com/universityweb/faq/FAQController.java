package com.universityweb.faq;

import com.universityweb.faq.request.FAQRequest;
import com.universityweb.faq.response.FAQResponse;
import com.universityweb.faq.service.FAQService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/faq")
@RestController
@Tag(name = "FAQs")
public class FAQController {
    @Autowired
    private FAQService faqService;

    @PostMapping("/create-faq")
    public ResponseEntity<FAQResponse> createFAQ(@RequestBody FAQRequest faqRequest) {
        return ResponseEntity.ok().body(faqService.createFAQ(faqRequest));
    }
    @PostMapping("/update-faq")
    public ResponseEntity<FAQResponse>  updateFAQ(@RequestBody FAQRequest faqRequest) {
        return ResponseEntity.ok().body(faqService.updateFAQ(faqRequest));
    }
    @PostMapping("/delete-faq")
    public String deleteFAQ(@RequestBody FAQRequest faqRequest) {
        faqService.deleteFAQ(faqRequest);
        return "FAQ deleted successfully";
    }

    @PostMapping("/get-all-faq-by-course")
    public ResponseEntity<List<FAQResponse>> getAllFAQByCourse(@RequestBody FAQRequest faqRequest) {
        return ResponseEntity.ok(faqService.getAllFAQByCourse(faqRequest));
    }
}

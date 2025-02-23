package com.universityweb.engagementservice.faq.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FAQRequest {
    private Long id;
    private Long courseId;
    private String question;
    private String answer;
}

package com.universityweb.faq.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

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

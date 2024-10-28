package com.universityweb.faq.response;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FAQResponse {
    private Long courseId;
    private Long id;
    private String question;
    private String answer;
}

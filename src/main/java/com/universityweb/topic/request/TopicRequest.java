package com.universityweb.topic.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest {
    private Long id;
    private String name;
}

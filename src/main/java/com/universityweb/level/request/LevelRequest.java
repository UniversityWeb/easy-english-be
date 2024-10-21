package com.universityweb.level.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelRequest {
    private Long id;
    private String name;
    private Long topicId;
    private String fromLevel;
    private String toLevel;
}

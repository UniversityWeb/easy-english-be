package com.universityweb.level.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelResponse {
    private Long id;
    private String name;
    private Long topicId;
    private String fromLevel;
    private String toLevel;
}

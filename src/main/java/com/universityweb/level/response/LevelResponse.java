package com.universityweb.level.response;

import com.universityweb.topic.response.TopicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelResponse {
    Long id;
    String name;
    Long topicId;
    String fromLevel;
    String toLevel;
    TopicResponse topic;
}

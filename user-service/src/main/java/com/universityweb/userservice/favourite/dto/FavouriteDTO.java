package com.universityweb.userservice.favourite.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavouriteDTO {
    Long id;
    LocalDateTime createdAt;
    String username;
    Long courseId;
}

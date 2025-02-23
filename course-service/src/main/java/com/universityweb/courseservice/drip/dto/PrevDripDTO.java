package com.universityweb.courseservice.drip.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrevDripDTO {
    Long id;
    String title;
    String type;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrevDripDTO that = (PrevDripDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type);
    }
}

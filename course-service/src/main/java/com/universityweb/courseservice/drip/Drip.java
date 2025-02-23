package com.universityweb.courseservice.drip;

import com.universityweb.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "course_drips")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Drip implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prev_id")
    private Long prevId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prev_type")
    private ESourceType prevType;

    @Column(name = "next_id")
    private Long nextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "next_type")
    private ESourceType nextType;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public enum ESourceType {
        LESSON,
        TEST
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drip drip = (Drip) o;
        return Objects.equals(prevId, drip.prevId) &&
                prevType == drip.prevType &&
                Objects.equals(nextId, drip.nextId) &&
                nextType == drip.nextType &&
                Objects.equals(course.getId(), drip.course.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(prevId, prevType, nextId, nextType, course.getId());
    }
}

package com.universityweb.testquestion.entity;

import com.universityweb.testpart.entity.TestPart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "test_questions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TestQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answers;

    @Column(name = "answer_key")
    private String answerKey;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private EType type;

    @ManyToOne
    @JoinColumn(name = "test_part_id")
    private TestPart testPart;

    public enum EType {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        MATCHING,
        FILL_BLANK
    }
}

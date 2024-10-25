package com.universityweb.questiongroup.entity;

import com.universityweb.testpart.entity.TestPart;
import com.universityweb.testquestion.entity.TestQuestion;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "test_question_groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QuestionGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "from_question")
    private Integer from;

    @Column(name = "to_question")
    private Integer to;

    private String title;

    private String requirement;

    @Column(name = "audio_path")
    private String audioPath;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "content_to_display", columnDefinition = "TEXT")
    private String contentToDisplay;

    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestQuestion> questions;

    @ManyToOne
    @JoinColumn(name = "test_part_id", referencedColumnName = "id")
    private TestPart testPart;
}

package com.universityweb.test.dto;

import com.universityweb.test.entity.Test;
import com.universityweb.testpart.dto.TestPartDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestDTO {
    private Long id;
    private Test.EType type;
    private Test.EStatus status;
    private String title;
    private String description;
    private Integer ordinalNumber;
    private Integer durationInMilis;
    private Double passingGrade;
    private LocalDateTime createdAt;
    private String audioPath;
    private List<TestPartDTO> parts;
    private Long sectionId;

    private Boolean isDone;
}

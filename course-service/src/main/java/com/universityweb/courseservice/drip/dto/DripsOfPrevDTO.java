package com.universityweb.courseservice.drip.dto;

import com.universityweb.drip.Drip;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DripsOfPrevDTO {
    Long id;
    Long prevId;
    Drip.ESourceType prevType;
    String prevTitle;
    String prevDetailType;
    Long courseId;
    List<DripOfPrevDTO> nextDrips;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class DripOfPrevDTO {
        private Long id;
        private Long nextId;
        private Drip.ESourceType nextType;
        private String nextTitle;
        private String nextDetailType;
    }
}

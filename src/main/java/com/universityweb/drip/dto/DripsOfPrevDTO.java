package com.universityweb.drip.dto;

import com.universityweb.drip.Drip;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DripsOfPrevDTO {
    private Long id;
    private Long prevId;
    private Drip.ESourceType prevType;
    private Long courseId;
    private List<DripOfPrevDTO> nextDrips;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class DripOfPrevDTO {
        private Long id;
        private Long nextId;
        private Drip.ESourceType nextType;
        private Boolean requiredCompletion;
    }
}

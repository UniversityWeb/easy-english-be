package com.universityweb.drip.dto;

import com.universityweb.drip.Drip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DripDTO {
    private Long id;
    private Long prevId;
    private Drip.ESourceType prevType;
    private Long nextId;
    private Drip.ESourceType nextType;
    private Boolean requiredCompletion;
    private Long courseId;

    private Object prev;
    private Object next;
}

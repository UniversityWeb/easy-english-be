package com.universityweb.listeningaudio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListeningAudioDTO {
    private Long id;
    private String filePath;
    private int durationInMilis;
    private Long testId;
}

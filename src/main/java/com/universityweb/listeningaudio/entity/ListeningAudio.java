package com.universityweb.listeningaudio.entity;

import com.universityweb.test.entity.Test;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "listening_audios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ListeningAudio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "duration_in_milis")
    private int durationInMilis;

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;
}

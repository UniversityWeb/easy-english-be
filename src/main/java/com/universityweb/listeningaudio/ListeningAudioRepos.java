package com.universityweb.listeningaudio;

import com.universityweb.listeningaudio.entity.ListeningAudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningAudioRepos extends JpaRepository<ListeningAudio, Long> {
}

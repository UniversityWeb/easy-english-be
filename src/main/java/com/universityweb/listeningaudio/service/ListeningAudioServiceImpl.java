package com.universityweb.listeningaudio.service;

import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import com.universityweb.listeningaudio.ListeningAudioMapper;
import com.universityweb.listeningaudio.ListeningAudioRepos;
import com.universityweb.listeningaudio.dto.ListeningAudioDTO;
import com.universityweb.listeningaudio.entity.ListeningAudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListeningAudioServiceImpl
        extends BaseServiceImpl<ListeningAudio, ListeningAudioDTO, Long, ListeningAudioRepos, ListeningAudioMapper>
        implements ListeningAudioService {

    @Autowired
    public ListeningAudioServiceImpl(ListeningAudioRepos repository) {
        super(repository, ListeningAudioMapper.INSTANCE);
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new RuntimeException("Could not find any listening audio with id: " + id);
    }

    @Override
    public ListeningAudioDTO update(Long id, ListeningAudioDTO dto) {
        ListeningAudio listeningAudio = getEntityById(id);

        listeningAudio.setFilePath(dto.getFilePath());
        listeningAudio.setDurationInMilis(dto.getDurationInMilis());

        ListeningAudio saved = repository.save(listeningAudio);
        return mapper.toDTO(saved);
    }
}

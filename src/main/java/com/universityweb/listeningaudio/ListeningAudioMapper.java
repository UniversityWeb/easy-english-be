package com.universityweb.listeningaudio;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.listeningaudio.dto.ListeningAudioDTO;
import com.universityweb.listeningaudio.entity.ListeningAudio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ListeningAudioMapper
        extends BaseMapper<ListeningAudio, ListeningAudioDTO> {
    ListeningAudioMapper INSTANCE = Mappers.getMapper(ListeningAudioMapper.class);

    @Mapping(source = "test.id", target = "testId")
    @Override
    ListeningAudioDTO toDTO(ListeningAudio entity);

    @Mapping(target = "test", ignore = true)
    @Override
    ListeningAudio toEntity(ListeningAudioDTO dto);
}

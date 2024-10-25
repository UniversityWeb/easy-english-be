package com.universityweb.listeningaudio;

import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.listeningaudio.dto.ListeningAudioDTO;
import com.universityweb.listeningaudio.entity.ListeningAudio;
import com.universityweb.listeningaudio.service.ListeningAudioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/listening-audio")
@Tag(name = "Listening Audio")
public class ListeningAudioController
        extends BaseController<ListeningAudio, ListeningAudioDTO, Long, ListeningAudioService> {

    @Autowired
    public ListeningAudioController(ListeningAudioService service) {
        super(service);
    }
}

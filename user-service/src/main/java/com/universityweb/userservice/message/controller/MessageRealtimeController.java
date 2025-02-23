package com.universityweb.userservice.message.controller;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.websocket.WebSocketConstants;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import com.universityweb.notification.controller.NotificationController;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequiredArgsConstructor
public class MessageRealtimeController {
    private static final Logger log = LogManager.getLogger(NotificationController.class);
    private final MessageService messageService;
    private final MediaService mediaService;

    @MessageMapping(WebSocketConstants.MESSAGE_DESTINATION)
    public void handleMessage(MessageDTO message) {
        log.info("Received message request: {}", message);

        if (message.getType() == Message.EType.IMAGE && message.getContent() != null && !message.getContent().isEmpty()) {
            try {
                MultipartFile multipartFile = new MultipartFile() {
                    @Override
                    public String getName() {
                        return "";
                    }

                    @Override
                    public String getOriginalFilename() {
                        return "";
                    }

                    @Override
                    public String getContentType() {
                        return "";
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public long getSize() {
                        return 0;
                    }

                    @Override
                    public byte[] getBytes() throws IOException {
                        return new byte[0];
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return null;
                    }

                    @Override
                    public void transferTo(File dest) throws IOException, IllegalStateException {

                    }
                };
                String fileUrl = mediaService.uploadFile(multipartFile);
                message.setContent(fileUrl);
            } catch (Exception e) {
                log.error(e);
            }
        }

        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);
        log.info("Sent message: {}", messageDTO);
    }
}

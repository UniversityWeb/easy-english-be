package com.universityweb.userservice.message.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages")
@RequiredArgsConstructor
public class MessageController {

    private final AuthService authService;
    private final MessageService messageService;
    private final MediaService mediaService;

    @GetMapping("/{senderUsername}/{recipientUsername}")
    public ResponseEntity<Page<MessageDTO>> getAllMessages(
            @PathVariable String senderUsername,
            @PathVariable String recipientUsername,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String curUsername = authService.getCurrentUsername();
        if (!senderUsername.equals(curUsername) && !recipientUsername.equals(curUsername)) {
            throw new PermissionDenyException("You do not have permission to access this message");
        }

        Page<MessageDTO> messages = messageService.getAllMessages(senderUsername, recipientUsername, page, size);
        return ResponseEntity.ok(MediaUtils.addMessageMediaUrls(mediaService, messages));
    }

    @GetMapping("/get-recent-chats")
    public ResponseEntity<Page<UserDTO>> getRecentChats(
            @RequestParam int page,
            @RequestParam int size
    ) {
        String curUsername = authService.getCurrentUsername();
        Page<UserDTO> users = messageService.getRecentChats(curUsername, page, size);
        return ResponseEntity.ok(MediaUtils.addUserMediaUrlsForPage(mediaService, users));
    }
}

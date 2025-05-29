package com.universityweb.message.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.message.service.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages")
@RequiredArgsConstructor
public class MessageController {

    private static final Logger log = LogManager.getLogger(MessageController.class);

    private final AuthService authService;
    private final MessageService messageService;
    private final MediaService mediaService;
    private final UserService userService;

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

    @PostMapping("/send")
    public ResponseEntity<Void> handleMessage(@RequestBody MessageDTO message) {
        log.info("Received message request: {}", message);

        if (message.getType() == Message.EType.IMAGE && message.getContent() != null && !message.getContent().isEmpty()) {
            try {
                String base64Str = message.getContent();
                String suffixUrl = mediaService.uploadFile(base64Str);
                message.setContent(suffixUrl);
            } catch (Exception e) {
                log.error(e);
            }
        }

        MessageDTO messageDTO = messageService.sendRealtimeMessage(message);

        //send auto msg
//        try {
//            String senderUsername = authService.getCurrentUsername();
//            String recipientUsername = messageDTO.getRecipientUsername();
//            User recipient = userService.loadUserByUsername(recipientUsername);
//            Message lastMsg = messageService.getLastMsg(senderUsername, recipientUsername);
//            LocalDateTime now = LocalDateTime.now();
//
//            if (recipient.getRole() == User.ERole.TEACHER
//                    && lastMsg != null
//                    && senderUsername.equals(lastMsg.getSender().getUsername())
////                    &&
//            ) {
//                messageService.sendAutoMessage(senderUsername, recipientUsername, now);
//            }
//        } catch (Exception e) {
//            log.error(e);
//        }

        log.info("Sent message: {}", messageDTO);
        return ResponseEntity.ok().build();
    }
}

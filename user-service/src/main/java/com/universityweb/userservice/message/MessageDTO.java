package com.universityweb.userservice.message;

import com.universityweb.common.auth.dto.UserDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDTO implements Serializable {
    UUID id;
    Message.EType type;
    String content;
    LocalDateTime sendingTime;
    String senderUsername;
    String recipientUsername;

    UserDTO recipient;
}

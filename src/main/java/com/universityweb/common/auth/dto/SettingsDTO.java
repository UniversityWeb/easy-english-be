package com.universityweb.common.auth.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsDTO {
    Boolean autoReplyEnabled = false;
    String autoReplyMessage = "I am currently unavailable. I will get back to you soon!";
}

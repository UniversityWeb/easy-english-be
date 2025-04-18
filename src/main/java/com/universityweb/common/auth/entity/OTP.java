package com.universityweb.common.auth.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTP {
    String otpStr;
    LocalDateTime expiryTime;

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting OTP to JSON", e);
        }
    }

    public static OTP fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(json, OTP.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to OTP", e);
        }
    }
}

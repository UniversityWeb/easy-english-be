package com.universityweb.common.auth.webauthn;

import com.webauthn4j.WebAuthnManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebAuthnConfig {

    @Bean
    public WebAuthnManager webAuthnManager() {
        return WebAuthnManager.createNonStrictWebAuthnManager();
    }
}

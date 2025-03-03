package com.universityweb.common.auth.webauthn;

import com.webauthn4j.WebAuthnManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webauthn")
public class WebAuthnController {

    @Autowired
    private WebAuthnManager webAuthnManager;

    @PostMapping("/register/start")
    public ResponseEntity<?> startRegistration(@RequestBody String username) {
        // Generate registration options
        return ResponseEntity.ok().body("Registration started");
    }

    @PostMapping("/register/finish")
    public ResponseEntity<?> finishRegistration(@RequestBody String attestationResponse) {
        // Verify registration
        return ResponseEntity.ok().body("Registration completed");
    }

    @PostMapping("/authenticate/start")
    public ResponseEntity<?> startAuthentication(@RequestBody String username) {
        // Generate authentication options
        return ResponseEntity.ok().body("Authentication started");
    }

    @PostMapping("/authenticate/finish")
    public ResponseEntity<?> finishAuthentication(@RequestBody String assertionResponse) {
        // Verify authentication
        return ResponseEntity.ok().body("Authentication completed");
    }
}

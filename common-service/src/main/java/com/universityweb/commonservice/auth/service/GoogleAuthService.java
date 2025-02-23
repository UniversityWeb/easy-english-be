package com.universityweb.common.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public GoogleIdToken.Payload verifyToken(String token) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(googleClientId)).build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            return idToken.getPayload();
        }
        return null;
    }

    public RegisterRequest getInfor(GoogleIdToken.Payload payload) {
        if (payload == null) return null;

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String gender = (String) payload.get("gender");

        if (name == null) name = "Unknown";
        if (picture == null) picture = "";
        if (gender == null) gender = "unknown";


        User.EGender userGender = "male".equals(gender) ? User.EGender.MALE : User.EGender.FEMALE;
        return new RegisterRequest(
                email,
                passwordEncoder.encode(email),
                name,
                email,
                "",
                userGender,
                LocalDate.now(),
                picture);
    }
}

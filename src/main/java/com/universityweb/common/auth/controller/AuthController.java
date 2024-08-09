package com.universityweb.common.auth.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.request.LoginRequest;
import com.universityweb.common.auth.response.LoginResponse;
import com.universityweb.common.auth.response.RegisterResponse;
import com.universityweb.common.auth.service.AuthService;
import com.universityweb.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register",
            description = "Add a new student by providing the necessary details in the request body.",
            responses = {
                    @ApiResponse(
                            description = "Student added successfully.",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody UserDTO userDTO
    ) {
        RegisterResponse registerResponse = authService.registerStudentAccount(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerResponse);
    }

    @Operation(
            summary = "Login",
            description = "Login into an account.",
            responses = {
                    @ApiResponse(
                            description = "Login successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Invalid credentials.",
                            responseCode = "401",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            description = "User disabled.",
                            responseCode = "403",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Logout",
            description = "Logout from an account.",
            responses = {
                    @ApiResponse(
                            description = "Logged out successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Invalid tokenStr.",
                            responseCode = "401",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenStr) {
        authService.logout(tokenStr);
        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(
            summary = "Get User by Token",
            description = "Retrieve user details based on the provided authentication token.",
            responses = {
                    @ApiResponse(
                            description = "User details retrieved successfully.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(
                            description = "Invalid or expired token.",
                            responseCode = "401",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/get-user-by-token")
    public ResponseEntity<UserDTO> getUserByTokenStr(@RequestParam String tokenStr) {
        UserDTO userDTO = authService.getUserByTokenStr(tokenStr);
        return ResponseEntity.ok(userDTO);
    }
}

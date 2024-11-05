package com.universityweb.common.auth.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class UserController {

    private static final Logger log = LogManager.getLogger(UserController.class);

    private final UserService userService;
    private final AuthService authService;
    private final MediaService mediaService;

    @Operation(
            summary = "Update User Profile",
            description = "Allows the current user to update their own profile information. The user must provide their own username in the request payload to perform the update.",
            responses = {
                    @ApiResponse(
                            description = "User profile updated successfully.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(
                            description = "Unauthorized: User is attempting to update information for a different user.",
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
    @PutMapping("/update-own-profile")
    public ResponseEntity<UserDTO> updateOwnProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        String usernameToUpdate = updateProfileRequest.getUsername();
        log.info("Received request to update user with username: {}", usernameToUpdate);

        authService.checkAuthorization(usernameToUpdate);

        UserDTO saved = userService.update(updateProfileRequest);
        log.info("Successfully updated user with username: {}", saved.getUsername());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/upload-avatar")
    public ResponseEntity<String> updateAudioFile(
            @RequestParam("avatar") MultipartFile avatar
    ) {
        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be null or empty");
        }

        User user = authService.getCurUser();
        mediaService.deleteFile(user.getAvatarPath());

        String suffixPath = mediaService.uploadFile(avatar);

        user.setAvatarPath(suffixPath);
        User saved = userService.save(user);
        return ResponseEntity.ok(mediaService.constructFileUrl(saved.getAvatarPath()));
    }
}

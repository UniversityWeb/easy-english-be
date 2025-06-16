package com.universityweb.common.auth.controller;

import com.universityweb.common.auth.dto.SettingsDTO;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.request.GetUserFilterReq;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.response.ErrorResponse;
import com.universityweb.common.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
public class UserController
        extends BaseController<User, UserDTO, String, UserService> {

    private final AuthService authService;
    private final MediaService mediaService;

    @Autowired
    public UserController(
            UserService service,
            AuthService authService,
            MediaService mediaService
    ) {
        super(service);
        this.authService = authService;
        this.mediaService = mediaService;
    }

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
        String username = authService.getCurrentUsername();
        log.info("Received request to update user with username: {}", username);
        UserDTO saved = service.update(updateProfileRequest);
        log.info("Successfully updated user with username: {}", saved.getUsername());
        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    @Override
    public ResponseEntity<Void> delete(String username) {
        if (!authService.getCurrentUsername().equals(username)) {
            throw new PermissionDenyException("Cannot delete another user");
        }
        return super.delete(username);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
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
        User saved = service.save(user);
        return ResponseEntity.ok(mediaService.constructFileUrl(saved.getAvatarPath()));
    }

    @PostMapping("/admin/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserForAdminDTO>> getUsersWithoutAdmin(
            @RequestBody GetUserFilterReq filterReq
    ) {
        Page<UserForAdminDTO> userDTOs = service.getUsersWithoutAdmin(filterReq);
        return ResponseEntity.ok(MediaUtils.addUserAdminMediaUrlsForPage(mediaService, userDTOs));
    }

    @PutMapping("/admin/update/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserForAdminDTO> updateUserForAdmin(
            @PathVariable String username,
            @RequestBody UserForAdminDTO req
    ) {
        UserForAdminDTO userDTO = service.updateUserForAdmin(username, req);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/admin/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserForAdmin(
            @PathVariable String username
    ) {
        service.delete(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserForAdminDTO> addUserForAdmin(
            @RequestBody UserForAdminDTO req
    ) {
        UserForAdminDTO userDTO = service.addUserForAdmin(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/upload-avatar/{username}")
    public ResponseEntity<String> updateAudioFileForAdmin(
            @PathVariable String username,
            @RequestParam("avatar") MultipartFile avatar
    ) {
        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.badRequest().body("File cannot be null or empty");
        }

        User user = service.loadUserByUsername(username);
        mediaService.deleteFile(user.getAvatarPath());

        String suffixPath = mediaService.uploadFile(avatar);

        user.setAvatarPath(suffixPath);
        User saved = service.save(user);
        return ResponseEntity.ok(mediaService.constructFileUrl(saved.getAvatarPath()));
    }

    @PutMapping("/update-own-settings")
    public ResponseEntity<UserDTO> updateUserSettings(
        @RequestBody SettingsDTO newSettingsDTO
    ) {
        User user = authService.getCurUser();
        log.info("Received request to update settings with username: {}", user.getUsername());

        SettingsDTO existingSettingsDTO = Utils.convertFromJson(user.getSettings(), SettingsDTO.class);

        if (newSettingsDTO.getAutoReplyMessage() == null) {
            assert existingSettingsDTO != null;
            newSettingsDTO.setAutoReplyMessage(existingSettingsDTO.getAutoReplyMessage());
        }
        if (newSettingsDTO.getAutoReplyEnabled() == null) {
            assert existingSettingsDTO != null;
            newSettingsDTO.setAutoReplyEnabled(existingSettingsDTO.getAutoReplyEnabled());
        }

        String jsonString = Utils.convertToJson(newSettingsDTO);
        user.setSettings(jsonString);
        UserDTO saved = service.savedAndConvertToDTO(user);

        log.info("Successfully updated settings with username: {}", saved.getUsername());
        return ResponseEntity.ok(saved);
    }
}

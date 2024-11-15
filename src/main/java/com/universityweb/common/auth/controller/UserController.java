package com.universityweb.common.auth.controller;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.PermissionDenyException;
import com.universityweb.common.auth.request.GetUserFilterReq;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping("/admin/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsersWithoutAdmin(
            @RequestBody GetUserFilterReq filterReq
    ) {
        Page<UserDTO> userDTOs = service.getUsersWithoutAdmin(filterReq);
        return ResponseEntity.ok(userDTOs);
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
        service.softDelete(username);
        return ResponseEntity.noContent().build();
    }
}

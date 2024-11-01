package com.universityweb.common.media;

import com.universityweb.common.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/media")
@Tag(name = "Media")
public class MediaController {

    private static final Logger log = LogManager.getLogger(MediaController.class);

    @Autowired
    private MediaService mediaService;

    @GetMapping("/get")
    public ResponseEntity<byte[]> getFileByName(@RequestParam("fileName") String fileName) throws Exception {
        byte[] fileContent = mediaService.getFile(fileName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    @Operation(summary = "Upload an image file", description = "Allows the user to upload an image.")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Custom file name", required = true)
            @RequestParam("customFileName") String customFileName
    ) {
        try {
            if (file.isEmpty()) {
                log.warn("File upload attempted with an empty file.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty.");
            }

            // Upload the file using mediaService
            mediaService.uploadFile(customFileName, file);
            log.info("File uploaded successfully: {}", customFileName);

            return ResponseEntity.ok("File uploaded successfully: " + customFileName);
        } catch (IOException e) {
            log.error("File upload failed due to I/O error.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed due to I/O error.");
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{fileName}")
    public ResponseEntity<Void> deleteFileByName(@PathVariable("fileName") String fileName) throws Exception {
        mediaService.deleteFile(fileName);
        log.info("File deleted successfully: {}", fileName);
        return ResponseEntity.noContent().build();
    }
}

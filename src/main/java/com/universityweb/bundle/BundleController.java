package com.universityweb.bundle;

import com.universityweb.bundle.req.BundleFilterReq;
import com.universityweb.bundle.service.BundleService;
import com.universityweb.common.infrastructure.BaseController;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jfr.Frequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/bundles")
@Tag(name = "Bundles")
public class BundleController
        extends BaseController<Bundle, BundleDTO, Long, BundleService> {

    private final MediaService mediaService;

    @Autowired
    public BundleController(
            BundleService service,
            MediaService mediaService
    ) {
        super(service);
        this.mediaService = mediaService;
    }

    @Override
    public void postCreate(BundleDTO createdDto) {
        createdDto = MediaUtils.attachBundleUrl(mediaService, createdDto);
    }

    @Override
    public void postGetById(BundleDTO createdDto) {
        createdDto = MediaUtils.attachBundleUrl(mediaService, createdDto);
    }

    @PostMapping("/update-image-preview/{id}")
    public ResponseEntity<BundleDTO> updateImagePreview(
            @PathVariable Long id,
            @RequestParam(value = "imagePreview") MultipartFile imagePreview
    ) {
        BundleDTO bundle = service.getById(id);

        if (imagePreview != null && !imagePreview.isEmpty()) {
            try {
                mediaService.deleteFile(bundle.getImagePreview());
                String savedImagePreview = mediaService.uploadFile(imagePreview);
                BundleDTO saved = service.updateImagePreview(id, savedImagePreview);
                return ResponseEntity.ok(saved);
            } catch (Exception e) {
                log.error("Failed to upload image file for course request: {}", e.getMessage(), e);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/get-my-bundles")
    public ResponseEntity<Page<BundleDTO>> getMyBundles(
            @RequestBody BundleFilterReq filterReq
    ) {
        Page<BundleDTO> bundles = service.getMyBundles(filterReq);
        return ResponseEntity.ok(bundles);
    }
}

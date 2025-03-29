package com.universityweb.bundle;

import com.universityweb.bundle.req.BundleFilterReq;
import com.universityweb.bundle.service.BundleService;
import com.universityweb.common.infrastructure.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bundles")
@Tag(name = "Bundles")
public class BundleController
        extends BaseController<Bundle, BundleDTO, Long, BundleService> {

    @Autowired
    public BundleController(BundleService service) {
        super(service);
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

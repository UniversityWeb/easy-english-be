package com.universityweb.bundle;

import com.universityweb.bundle.service.BundleService;
import com.universityweb.common.infrastructure.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
}

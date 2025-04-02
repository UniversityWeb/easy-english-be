package com.universityweb.bundle.service;

import com.universityweb.bundle.Bundle;
import com.universityweb.bundle.BundleDTO;
import com.universityweb.bundle.req.BundleFilterReq;
import com.universityweb.common.infrastructure.service.BaseService;
import org.springframework.data.domain.Page;

public interface BundleService extends BaseService<Bundle, BundleDTO, Long> {
    Page<BundleDTO> getMyBundles(BundleFilterReq filterReq);

    BundleDTO updateImagePreview(Long id, String savedImagePreview);
}

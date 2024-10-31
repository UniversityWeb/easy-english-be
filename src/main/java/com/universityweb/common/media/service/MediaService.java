package com.universityweb.common.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String uploadFile(String customFileName, MultipartFile file) throws Exception;
    byte[] getFile(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
}

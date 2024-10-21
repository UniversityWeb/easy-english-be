package com.universityweb.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadFileService {
    String uploadFile(MultipartFile multipartFile) throws IOException;
}

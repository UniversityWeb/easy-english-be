package com.universityweb.commonservice.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    /**
     * Uploads a file
     *
     * This method validates the provided file, generates a unique filename
     * to prevent name collisions, and then uploads the file to the specified
     * MinIO bucket. The unique filename is based on the original filename
     * combined with a timestamp to ensure it is unique across uploads.
     *
     * @param file the MultipartFile object representing the file to be uploaded
     * @return a String representing the suffix path of the uploaded file in
     *         the format "/<uniqueFileName>", where <uniqueFileName>
     *         includes the original file extension
     * @throws IllegalArgumentException if the file validation fails (e.g.,
     *         if the file is null or exceeds the maximum allowed size)
     * @throws RuntimeException if an error occurs during the upload process,
     *         such as issues with MinIO storage or IO exceptions
     */
    String uploadFile(MultipartFile file);

    byte[] getFile(String suffixPath);

    void deleteFile(String suffixPath);

    /**
     * Constructs the full URL for the uploaded file based on its suffix path.
     *
     * @param suffixPath the unique suffix path of the uploaded file
     * @return a String representing the full URL of the file
     */
    String constructFileUrl(String suffixPath);
}

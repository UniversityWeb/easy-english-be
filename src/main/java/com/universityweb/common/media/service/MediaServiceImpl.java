package com.universityweb.common.media.service;

import com.universityweb.common.exception.CustomException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        return uploadToMinio(file, uniqueFileName);
    }

    @Override
    public String uploadFile(String base64Str) {
        try {
            String[] parsedData = parseBase64Data(base64Str);
            String contentType = parsedData[0];
            String pureBase64 = parsedData[1];

            String objectName = generateUniqueFileName(contentType.replace('/', '.'));

            byte[] decodedBytes = Base64.getDecoder().decode(pureBase64);
            return uploadToMinio(decodedBytes, contentType, objectName);
        } catch (MinioException e) {
            throw new CustomException("Minio error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new CustomException("Invalid base64: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getFile(String suffixPath) {
        if (suffixPath == null || suffixPath.isEmpty()) return null;

        String uniqueFileName = extractFileName(suffixPath);
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFileName)
                        .build()
        )) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (ServerException | XmlParserException | InternalException | InvalidResponseException |
                 InvalidKeyException | NoSuchAlgorithmException | IOException | ErrorResponseException |
                 InsufficientDataException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public void deleteFile(String suffixPath) {
        if (suffixPath == null || suffixPath.isEmpty()) return;

        try {
            String uniqueFileName = extractFileName(suffixPath);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public String constructFileUrl(String suffixPath) {
        if (suffixPath == null || suffixPath.startsWith("http://") || suffixPath.startsWith("https://")) {
            return suffixPath; // Return the original suffix path as is
        }
        return minioUrl + "/" + bucketName + suffixPath;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        if (file.getSize() > 4L * 1024 * 1024 * 1024) { // Max size 4 GB
            throw new IllegalArgumentException("File must not exceed 4 GB");
        }
    }

    private String[] parseBase64Data(String base64Data) {
        if (base64Data.startsWith("data:")) {
            String[] parts = base64Data.split(",", 2);
            String[] header = parts[0].split(";");
            String contentType = header[0].substring(5);
            return new String[]{contentType, parts[1]};
        }
        return new String[]{"application/octet-stream", base64Data};
    }

    private String resolveFileExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "application/pdf" -> "pdf";
            default -> "bin";
        };
    }

    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + fileExtension; // Use UUID for unique name
    }

    private String uploadToMinio(MultipartFile file, String uniqueFileName) {
        try (InputStream fileStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(fileStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return "/" + uniqueFileName;
        } catch (MinioException e) {
            throw new CustomException("MinIO error occurred during file upload" + e.getMessage());
        } catch (IOException e) {
            throw new CustomException("Failed to read file input stream" + e.getMessage());
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new CustomException("Error while uploading file to MinIO" + e.getMessage());
        }
    }

    private String extractFileName(String suffixPath) {
        return suffixPath.startsWith("/") ? suffixPath.substring(1) : suffixPath;
    }

    private String uploadToMinio(byte[] data, String contentType, String objectName) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .contentType(contentType)
                        .build());
        return "/" + objectName;
    }
}

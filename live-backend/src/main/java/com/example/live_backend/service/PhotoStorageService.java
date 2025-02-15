package com.example.live_backend.service;

import java.net.URI;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoStorageService {
    
    private final AmazonS3 amazonS3;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public String savePhoto(MultipartFile file) {
        // 1) Generate a random key/filename
        String key = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 2) Upload to S3
            amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));

            // 3) Return S3 file URL
            // This is a typical approach if the bucket is public or you handle the policy
            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to upload file to S3", ex);
        }
    }

    public void deletePhoto(String photoUrl) {
        // 1) We need to parse the "key" from the URL or store the key separately
        // If you're storing the entire S3 URL in DB, parse out the last part as "key".
        String key = parseKeyFromUrl(photoUrl);
        amazonS3.deleteObject(bucketName, key);
    }

    private String parseKeyFromUrl(String photoUrl) {
        // Example: https://my-photos-bucket.s3.amazonaws.com/uuid_filename.jpg
        // You can parse the substring after the bucket domain:
        // or, simpler, store the 'key' in DB instead of the full URL
        URI uri = URI.create(photoUrl);
        // e.g. "/uuid_filename.jpg"
        String path = uri.getPath(); 
        // remove leading slash
        return path.startsWith("/") ? path.substring(1) : path;
    }
}

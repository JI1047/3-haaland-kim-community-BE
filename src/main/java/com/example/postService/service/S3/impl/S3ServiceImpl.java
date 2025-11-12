package com.example.postService.service.S3.impl;

import com.example.postService.service.S3.S3Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

public class S3ServiceImpl implements S3Service {

    private final S3Presigner presigner;

    private final String bucketName = "haaland-bucket";

    public S3ServiceImpl(S3Presigner s3Presigner) {
        this.presigner = s3Presigner;
    }

    public String generatePresignedUrl(String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/jpeg")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3)) // 3분간 유효
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        URL presignedUrl = presignedRequest.url();
        return presignedUrl.toString();
    }
}

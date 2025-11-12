package com.example.postService.service.S3;

public interface S3Service {

    String generatePresignedUrl(String originalFileName);
}

package com.example.postService.service.S3.impl;

import com.example.postService.service.S3.S3Service;
import com.example.postService.util.FileNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Presigner presigner;
    private final String bucketName = "haaland-bucket";

    @Override
    public String generatePresignedUrl(String originalFileName) {
        String safeFileName = FileNameUtil.sanitizeFileName(originalFileName, "profileImage");
        String key = "public/image/profile/" + safeFileName;

        //  확장자 추출
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalFileName.length() - 1) {
            extension = originalFileName.substring(dotIndex + 1).toLowerCase();
        }

        //  확장자별 Content-Type 매핑
        String contentType = switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream"; // 확장자를 모를 때 안전한 기본값
        };

        // Presigned URL 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }
}

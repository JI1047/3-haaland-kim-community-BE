package com.example.postService.controller;


import com.example.postService.service.S3.S3Service;
import com.example.postService.util.FileNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * S3Controller
 * ---------------------------------------------------------
 * 프론트엔드에서 파일명을 전달받아
 * AWS S3 업로드용 Presigned URL을 생성하고 반환하는 컨트롤러
 *
 * 1. 클라이언트가 ?fileName=... 으로 요청
 * 2. 서버가 안전한 파일명 생성 후 Presigned URL 생성
 * 3. JSON 형태로 url + key + fileName 반환
 * ---------------------------------------------------------
 */
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * Presigned URL 생성 엔드포인트
     */
    @GetMapping("/presigned")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestParam String fileName) {
        // 안전한 파일명 생성
        String safeFileName = FileNameUtil.sanitizeFileName(fileName, "profileImage");
        // Presigned URL 생성
        String presignedUrl = s3Service.generatePresignedUrl(fileName);
        // S3 내부 key 경로
        String key = "public/image/profile/" + safeFileName;

        // JSON 형태로 응답
        return ResponseEntity.ok(Map.of(
                "url", presignedUrl,
                "key", key,
                "fileName", safeFileName
        ));
    }
}
package com.example.postService.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 파일 저장 유틸리티 클래스
 * - 사용자가 업로드한 MultipartFile을 로컬 서버 디렉토리에 저장하고
 * - 저장된 파일명을 반환
 */
@Component
public class FileStorage {

    @Value("${file.upload-dir}")
    private String uploadDir; // application.yml에서 주입됨

    /**
     * MultipartFile을 로컬 디렉토리에 저장하고 파일명을 반환한다.
     * 파일 이름 중복 방지를 위해 timestamp를 붙임
     */
    public String storeFile(MultipartFile file) throws IOException {
        // 저장 경로를 절대 경로로 변환 후 정규화
        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // uploads 폴더가 없으면 자동 생성
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 파일명 중복 방지를 위해 timestamp를 붙여서 이름 지정
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetPath = dirPath.resolve(fileName);

        // 파일 저장 (기존 이름 중복 시 덮어쓰기)
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // 저장된 파일명 반환
    }
}

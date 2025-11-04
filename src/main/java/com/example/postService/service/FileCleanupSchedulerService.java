package com.example.postService.service;

import com.example.postService.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileCleanupSchedulerService {

    private final UserJpaRepository userJpaRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @Scheduled(fixedRate = 30000)
    @Transactional(readOnly = true) // ✅ 세션 유지
    public void cleanup() {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists() || !uploadDir.isDirectory()) {
                return;
            }

            List<String> validImagePaths = userJpaRepository.findAll().stream()
                    .map(u -> u.getUserProfile().getProfileImage())
                    .filter(img -> img != null && !img.isEmpty())
                    .toList();

            for (String path : validImagePaths) {
                System.out.println(path);
            }
            File[] files = uploadDir.listFiles();
            if (files == null) return;


            for (File file : files) {
                String fileName = file.getName();
                boolean matched = false;

                for (String path : validImagePaths) {
                    try {
                        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
                        String decodedFileName = decodedPath.substring(decodedPath.lastIndexOf('/') + 1);

                        String normalizedDBName = Normalizer.normalize(decodedFileName, Normalizer.Form.NFC);
                        String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);

                        System.out.println("DB파일명: " + normalizedDBName);
                        System.out.println("실제파일명: " + normalizedFileName);

                        if (normalizedDBName.equals(normalizedFileName)) {
                            matched = true;
                            break;
                        }

                    } catch (Exception e) {
                        System.err.println("[DECODE ERROR] " + e.getMessage());
                    }
                }

                if (!matched) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        System.out.println("[CLEANUP] 삭제된 고아 파일: " + fileName);
                    }
                } else {
                    System.out.println("[KEEP] 유지된 파일: " + fileName);
                }
                System.out.println("----------");
            }


        } catch (Exception e) {
            System.err.println("[CLEANUP ERROR] 파일 정리 중 오류: " + e.getMessage());
        }
    }
}
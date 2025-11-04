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

/**
 * 이미지 업로드 과정에서 upload파일에 생긴 고아파일(DB에 등록되지 않은 이미지) 1분 주기로 삭제하는 로직
 * 사용자(User)의 프로필 이미지로 등록된 파일명만을 유지하고
 * 그 외의 파일은 삭제하여 저장 공간 낭비를 방지함
 *
 * 1. uploads 디렉터리 확인 및 파일 목록 불러오기
 * 2. DB에서 모든 User의 프로필 이미지 경로 조회
 * 3. 파일명 비교(정규화 및 디코딩 포함)
 * 4. 매칭되지 않는 파일 삭제 (로그 출력)
 */
@Service
@RequiredArgsConstructor
public class FileCleanupSchedulerService {

    private final UserJpaRepository userJpaRepository;

    //업로드된 이미지 파일들이 저장되는 기본 경로
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 5분마다 실행되는 스케줄러
     */
    @Scheduled(fixedRate = 300000)// 5분마다 실행되도록 변경
    @Transactional(readOnly = true) //  세션 유지
    public void cleanup() {
        try {
            //upload 폴더 존재 여부 확인
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists() || !uploadDir.isDirectory()) {
                return;            //없다면 예외 처리
            }

            //DB에 저장된 모든 사용자 프로필 이미지 URL 조회
            List<String> validImagePaths = userJpaRepository.findAll().stream()
                    .map(u -> u.getUserProfile().getProfileImage())
                    .filter(img -> img != null && !img.isEmpty())
                    .toList();

            //upload파일에 있는 이미지 파일을 저장할 배열선언 후 가져오기
            File[] files = uploadDir.listFiles();
            if (files == null) return;


            //files만큼 for문을 통해 DB에 저장된 UserProfileImage와 파일명 비교
            for (File file : files) {
                String fileName = file.getName();
                boolean matched = false;

                for (String path : validImagePaths) {
                    try {
                        // URL 디코딩 → 한글, 특수문자 인코딩 문제 방지
                        String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);

                        // 파일명만 추출 ("/uploads/" 이후 부분)
                        String decodedFileName = decodedPath.substring(decodedPath.lastIndexOf('/') + 1);

                        // Mac/Windows/Linux 간 문자열 정규화(NFC/NFD) 문제 방지
                        String normalizedDBName = Normalizer.normalize(decodedFileName, Normalizer.Form.NFC);
                        String normalizedFileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);

                        // DB의 파일명과 실제 파일명이 일치하면 유지
                        if (normalizedDBName.equals(normalizedFileName)) {
                            matched = true;
                            break;
                        }

                    } catch (Exception e) {
                        System.err.println("[DECODE ERROR] " + e.getMessage());
                    }
                }

                //매칭되지 않는 경우 삭제 처리
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
package com.example.postService.util;

public class FileNameUtil {

    public static String sanitizeFileName(String originalName, String prefix) {
        //확장자 추출
        String ext = "";
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalName.length() - 1) {
            ext = originalName.substring(dotIndex + 1).toLowerCase();
        }

        //파일명 안전하게 (한글/공백/특수문자 제거)
        String baseName = originalName.substring(0, dotIndex)
                .replaceAll("[^a-zA-Z0-9]", ""); // 영문+숫자만 남김

        if (baseName.isEmpty()) baseName = "file";

        //고유 suffix 생성 (시간 + 난수)
        String uniqueSuffix = System.currentTimeMillis() + "-" + Math.round(Math.random() * 1e9);

        //최종 이름 조합
        return String.format("%s-%s.%s", prefix, uniqueSuffix, ext);
    }
}

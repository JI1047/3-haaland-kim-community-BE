package com.example.postService.config;

import com.example.postService.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;//로그인 체크 인터셉터

    /**
     * InterceptorRegistry을 통해 전역 인터셉터 등록
     * 기본적으로 모든 API 요청에 세션 검증 적용
     * 인증 필요 없는 경로는 예외 설정
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/api/**")//모든 api가 세션 필요로 설정
                .excludePathPatterns("/api/users/login",
                        "/api/users",
                        "/api/users/sign-up",
                        "/api/users/profile",
                        "/api/users/password",
                        "/api/posts/list",
                        "/api/posts/{postId}",
                        "/api/posts/{postId}/update",
                        "/api/terms"
                );//로그인,회원가입,게시물 목록 조회, 게시물 상세 조회는 세션 없이 진행되도록 예외 처리
    }

    /**
     * CORS 설정
     * 프론트엔드(JS, React 등)에서 오는 요청 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

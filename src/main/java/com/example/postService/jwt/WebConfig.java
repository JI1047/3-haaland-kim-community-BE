package com.example.postService.jwt;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 서블릿 필터를 애플리케이션에 등록하는 스프링 설정 클래스
 * @Configuration으로 설정 클래스로 인식되고
 * @RequiredArgsConstructor 로 JwtAuthFilter가 생성자가 주입됨
 * 컨트롤러 진입 전 요청을 가로채 토큰 추출.검증을 수행하고, 유효 시 사용자 정보를 컨텍스트/요청 속성에 저장함
 * URL 패턴을 통해서 특정 API에만 JWT 검증을 적용할 수 있다.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * FilterRegistrationBean을 통해 커스텀 필터를 서블릿 컨테이너에 명시적으로 등록됨
     * setFilter(jwtAuthFilter)는 JWT를 검증하는 필터 인스턴스를 체인에 연결
     * `addUrlPatterns("/*")`로 모든 요청 경로에 필터가 적용
     * `setOrder(1)`로 필터 실행 순서를 지정해, 다른 필터보다 우선 실행
     */
    @Bean
    public FilterRegistrationBean<Filter> jwtFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtAuthFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    /**
     * 업로드된 파일을 정적 리소스로 서빙하기 위한 핸들러 설정
     * 1. 업로드된 실제 디렉토리 경로를 절대 경로로 변환
     * 2. "/uploads/**" 요청 경로를 해당 로컬 디렉토리로 연결시킴
     * 3. 결과적으로 http://localhost:8080/uploads/파일명 으로 접근 가능하게 함
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //로컬에 실제 파일이 저장된 uploads 폴더를 절대 경로로 변환
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        //절대 경로를 Spring이 인식할 수 있도록 URI 형태로 변환
        String uploadPath = uploadDir.toUri().toString();

        // "/uploads/**"로 들어오는 모든 요청을 uploadPath 경로로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
    /**
     * CORS 설정
     * 프론트엔드(JS, React 등)에서 오는 요청 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500",
                        "http://13.124.52.101:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

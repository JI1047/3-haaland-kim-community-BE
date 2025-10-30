package com.example.postService.session;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

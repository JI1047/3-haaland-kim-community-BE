package com.example.postService.session;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//로그인 된 사용자 인지 세션을 통해 체크하는 인터셉터
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        //기존 세션이 존재하는지 확인 (새로 생성 x)
        HttpSession session = request.getSession(false);

        //세션이 없거나 세션에 해당하는 user가 없을 시 에러 처리
        if(session == null || session.getAttribute("user") == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//응답 코드 생성

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"로그인이 필요합니다.\"}");// 에러 메세지 반환
            return false;
        }
        return true;
    }
}

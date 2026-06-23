package com.example.demo.handler;

import com.example.demo.domain.jwt.service.JwtService;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Component//bean으로 등록되도록
@Qualifier("LoginSuccessHandler")//social과 중복을 막기 위해
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    public LoginSuccessHandler(JwtService jwtService){
        this.jwtService=jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)throws IOException, ServletException{
        //username, role
        String username=authentication.getName();
        String role=authentication.getAuthorities().iterator().next().getAuthority();
        //authentication.getAuthorities()는 권한 목록 전체를 반환 iterator().next()는 첫번째 것만 사용한다. .getAuthority()는 꺼내는 역할
        //jwt(access/refresh)발급
        String accessToken= JWTUtil.createJWT(username, role,true);
        String refreshToken=JWTUtil.createJWT(username,role,false);

        //발급한 refresh db테이블 저장
        jwtService.addRefresh(username,refreshToken);

        //응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json=String.format("{\"accessToken\":\"%s\",\"refreshToken\":\"%s\"}",accessToken,refreshToken);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
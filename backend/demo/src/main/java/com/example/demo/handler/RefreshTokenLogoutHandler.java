package com.example.demo.handler;

import com.example.demo.domain.jwt.service.JwtService;
import com.example.demo.util.JWTUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RefreshTokenLogoutHandler implements LogoutHandler {
    private final JwtService jwtService;

    public RefreshTokenLogoutHandler(JwtService jwtService) {
        this.jwtService=jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try{
            String body=new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines().reduce("",String::concat);
            if(!StringUtils.isEmpty(body)){
                return;
            }

            ObjectMapper mapper=new ObjectMapper();
            JsonNode jsonNode=mapper.readTree(body);
            String refreshToken=jsonNode.has("refreshToken")? jsonNode.get("refreshToken").asText():null;

            if(refreshToken==null){
                return;
            }
            Boolean isValid= JWTUtil.isValid(refreshToken,false);
            if(!isValid){
                return;
            }

            jwtService.removeRefresh(refreshToken);
        }catch (IOException e){
            throw new RuntimeException("Failed to refresh token",e);
        }
    }
}

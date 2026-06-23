package com.example.demo.filter;

import com.example.demo.domain.jwt.service.JwtService;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JWTFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    // 생성자 주입
    public JWTFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String authorization=request.getHeader("Authorization");
        if(authorization==null){
            filterChain.doFilter(request,response);
            return;
        }
        if(!authorization.startsWith("Bearer ")){
            throw new ServletException("Invalid jwt token");
        }

        String accessToken=authorization.split(" ")[1];

        if(JWTUtil.isValid(accessToken,true)){
            String username=JWTUtil.getUsername(accessToken);
            String role=JWTUtil.getRole(accessToken);

            List<GrantedAuthority> authorites=
                    Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_"+role)
                    );

            Authentication auth=new UsernamePasswordAuthenticationToken(username,null,authorites);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request,response);
        }
        else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("error 토큰 만료 또는 유효하지 않은 토큰");
        }
    }
}

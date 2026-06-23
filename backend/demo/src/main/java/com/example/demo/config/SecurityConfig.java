package com.example.demo.config;

import com.example.demo.domain.jwt.service.JwtService;
import com.example.demo.domain.user.entity.UserRoleType;
import com.example.demo.filter.JWTFilter;
import com.example.demo.filter.LoginFilter;
import com.example.demo.handler.RefreshTokenLogoutHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final JwtService jwtService;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,AuthenticationSuccessHandler loginSuccessHandler,JwtService jwtService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.jwtService = jwtService;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //권한 계층
    @Bean
    public RoleHierarchy roleHierarchy(){
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.name()).implies(UserRoleType.USER.name())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173"); // 리액트 주소
        corsConfiguration.addAllowedMethod("*"); // 모든 Method(POST, GET 등) 허용
        corsConfiguration.addAllowedHeader("*"); // 모든 Header 허용
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();//url 패턴 기반 cors설정 관리 저장소
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
// 1. 기초 설정
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 2. 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/jwt/exchange", "/jwt/refresh", "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/exist", "/user").permitAll()
                        .requestMatchers("/drama/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/board/**").authenticated()
                        .requestMatchers("/drama/search").authenticated()
                        .requestMatchers(
                                "/id/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        // 3. 필터 등록 (이 부분이 에러의 핵심!)
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler);

        // 그 바로 앞에 JWTFilter를 둡니다.
        http.addFilterBefore(new JWTFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        // 기준점: UsernamePasswordAuthenticationFilter (스프링 기본 필터)
        // 그 자리에 LoginFilter를 갈아 끼웁니다.
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 4. 예외 처리
        http.exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                })
        );
        return http.build();
    }
}


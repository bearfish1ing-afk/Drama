package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir= Paths.get("uploads");//컴퓨터 내부 경로 물리
        String uploadPath=uploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/**")//이미지 주소인 걸 확인
                .addResourceLocations("file:///" + uploadPath + "/");//내 컴퓨터의 실제 폴더 위치를 알려줌
        /*+++++추가+++++
        클라우드 저장소를 사용해서 사진을 올리는 것을 해야지 내가 컴퓨터에서 사진을 지울 경우에도 
        사진을 유지할 수 있다
         */
    }

}


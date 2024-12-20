package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //uploadPath 값을 읽어옴
    @Value("${uploadPath}")
    String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //url에 /images로 시작하는 경우 uploadPath에 설정한 폴더 기준으로 파일 읽어오기
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);  //file:///C:/shop/
    }

}

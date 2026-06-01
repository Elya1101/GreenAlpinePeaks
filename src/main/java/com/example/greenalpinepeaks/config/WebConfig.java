package com.example.greenalpinepeaks.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Настройка для доступа к загруженным файлам
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");

        // Для доступа к статическим ресурсам
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/");
    }
}
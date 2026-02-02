package com.watch.watch_mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件路径配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPath = System.getProperty("user.dir");
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + projectPath + "/upload/");
    }
}
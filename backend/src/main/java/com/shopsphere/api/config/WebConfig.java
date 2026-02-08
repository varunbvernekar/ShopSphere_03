package com.shopsphere.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get("uploads");
        String absolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");

    }
}

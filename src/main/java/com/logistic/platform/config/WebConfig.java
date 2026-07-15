package com.logistic.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${logistic.pod.save.path}")
    private String podSavedPath;

    @Value("${logistic.pod.get.url}")
    private String podGetUrl;

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        registry.addResourceHandler(podGetUrl + "/**")
                .addResourceLocations("file:" + podSavedPath + "/");
    }
}

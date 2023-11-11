package com.example.customer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
class WebMvcConfig implements WebMvcConfigurer

{

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)

    {
        configurer.mediaType("js", MediaType.APPLICATION_JSON);
    }
}

package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
               .addPathPatterns("/**")
               .excludePathPatterns("/api/**", "/", "/index.html", "/shopping-cart.html", "/login.html", "/register.html","/forgot-password.html", "/reset-password.html");
    }
}
package com.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class UrlConfig {
    
    @Value("${app.frontend.url}")
    private String defaultFrontendUrl;
    
    @Value("#{'${app.allowed.origins}'.split(',')}")
    private List<String> allowedOrigins;
    
    public String getFrontendUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
                
        // 從請求中獲取 Origin 或 Referer
        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = request.getHeader("Referer");
        }
        
        // 如果沒有 Origin 也沒有 Referer，使用 Host
        if (origin == null) {
            String host = request.getHeader("Host");
            if (host != null) {
                origin = request.getScheme() + "://" + host;
            }
        }
        
        // 如果找到來源網址且在允許清單中，則使用該網址
        if (origin != null) {
            String finalOrigin = origin.replaceAll("/+$", ""); // 移除尾端斜線
            if (allowedOrigins.contains(finalOrigin)) {
                return finalOrigin;
            }
        }
        
        // 如果都找不到合適的網址，使用預設值
        return defaultFrontendUrl;
    }
}
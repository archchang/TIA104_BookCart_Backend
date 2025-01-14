package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
     // 允許的來源（前端應用的URL）
        config.addAllowedOrigin("http://localhost:80");    // 如果使用80端口
        config.addAllowedOrigin("http://localhost");       // 如果使用80端口（省略寫法）
        config.addAllowedOrigin("http://127.0.0.1:80");   // 本地IP訪問
        config.addAllowedOrigin("http://127.0.0.1");      // 本地IP訪問（省略寫法）
        
        // 允許帶憑證（cookies）
        config.setAllowCredentials(true);
        
        // 允許的 HTTP 方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允許的請求頭
        config.addAllowedHeader("*");
        
        // 暴露的響應頭
        config.addExposedHeader("Authorization");
        
        // 預檢請求的有效期，單位為秒
        config.setMaxAge(3600L);
        
        // 註冊配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
	@Value("${app.allowed.origins}")
	private String allowedOrigins;
	
	@Value("${app.frontend.url}")
	private String frontendUrl;
	
	@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        
        //從properties檔案中讀取並設置允許的來源
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        origins.forEach(origin -> {
        	config.addAllowedOrigin(origin);
        	
        	if(!origin.contains(":")) {
        		config.addAllowedOrigin(origin + ":80");
        	}
        });
        
        config.addAllowedOrigin(frontendUrl);
        if(!frontendUrl.contains(":")) {
        	config.addAllowedOrigin(frontendUrl + ":80");
        }
        
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
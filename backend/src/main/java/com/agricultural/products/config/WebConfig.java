package com.agricultural.products.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 处理跨域、静态资源映射和缓存策略
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // 缓存控制常量
    private static final String CACHE_CONTROL_IMAGES = "public, max-age=31536000, immutable"; // 1年
    private static final String CACHE_CONTROL_STATIC = "public, max-age=86400"; // 1天
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 图片资源 - 长期缓存，因为文件名包含哈希
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:D:/upload/")
                .setCachePeriod(31536000) // 1年（秒）
                .resourceChain(true);
        
        // 静态资源（CSS, JS等）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(86400); // 1天
    }
}

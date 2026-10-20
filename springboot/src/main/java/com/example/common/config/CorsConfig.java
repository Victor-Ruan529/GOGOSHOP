package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置：只允许配置文件中列出的前端地址访问，不再使用 *
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(GogoshopProperties properties) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        properties.getCors().getAllowedOrigins().forEach(corsConfiguration::addAllowedOrigin);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

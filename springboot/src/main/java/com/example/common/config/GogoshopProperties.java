package com.example.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * application.yml 中 gogoshop.* 的自定义配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "gogoshop")
public class GogoshopProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private File file = new File();
    private Login login = new Login();

    @Data
    public static class Jwt {
        private String secret;
        private int expireHours = 2;
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Data
    public static class File {
        private String uploadDir;
        private String baseUrl = "http://localhost:9090";
        private List<String> allowedExtensions = new ArrayList<>();
    }

    @Data
    public static class Login {
        private int maxFailures = 5;
        private int lockMinutes = 10;
    }
}

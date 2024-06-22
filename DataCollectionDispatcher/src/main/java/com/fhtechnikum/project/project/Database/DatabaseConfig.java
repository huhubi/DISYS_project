package com.fhtechnikum.project.project.Database;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseConfig {

    private Map<String, DataSourceProperties> datasources;

    @Data
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
    }
}

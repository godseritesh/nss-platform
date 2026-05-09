package com.nssplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            // Fallback for local development
            return DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/nssdb")
                    .username("postgres")
                    .password("postgres")
                    .build();
        }

        try {
            // Convert postgres://user:pass@host:port/db to JDBC format
            URI dbUri = new URI(databaseUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            
            // Transform postgresql:// or postgres:// to jdbc:postgresql://
            String scheme = dbUri.getScheme();
            if (!scheme.startsWith("jdbc:")) {
                scheme = "jdbc:postgresql";
            }
            
            String jdbcUrl = String.format("%s://%s:%d%s", 
                    scheme, dbUri.getHost(), dbUri.getPort(), dbUri.getPath());

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException("Invalid DATABASE_URL format", e);
        }
    }
}

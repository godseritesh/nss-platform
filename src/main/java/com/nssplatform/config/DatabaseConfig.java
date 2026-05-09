package com.nssplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        System.out.println("Detected DATABASE_URL: " + (databaseUrl != null ? "FOUND" : "MISSING"));

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            return DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/nssdb")
                    .username("postgres")
                    .password("postgres")
                    .build();
        }

        try {
            // Remove any existing jdbc: prefix to avoid double-prefixing
            String cleanUrl = databaseUrl.replace("jdbc:postgresql://", "").replace("postgresql://", "").replace("postgres://", "");
            
            // Format: user:password@host:port/database
            String[] parts = cleanUrl.split("@");
            String credentials = parts[0];
            String hostInfo = parts[1];
            
            String username = credentials.split(":")[0];
            String password = credentials.split(":")[1];
            
            String jdbcUrl = "jdbc:postgresql://" + hostInfo;
            
            System.out.println("Connecting to JDBC URL: jdbc:postgresql://" + hostInfo.split("/")[0]);

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } catch (Exception e) {
            System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
            // Last resort: return builder and let Spring try to handle it
            return DataSourceBuilder.create().build();
        }
    }
}

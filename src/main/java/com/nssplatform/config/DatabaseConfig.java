package com.nssplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (databaseUrl == null) databaseUrl = System.getenv("DATABASE_URL");
        
        System.out.println("--- DATABASE CONNECTION ATTEMPT ---");
        
        HikariConfig config = new HikariConfig();
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/nssdb");
            config.setUsername("postgres");
            config.setPassword("postgres");
        } else {
            // Fix URL format if needed
            String jdbcUrl = databaseUrl;
            if (jdbcUrl.startsWith("postgres://")) {
                jdbcUrl = jdbcUrl.replace("postgres://", "jdbc:postgresql://");
            } else if (jdbcUrl.startsWith("postgresql://")) {
                jdbcUrl = jdbcUrl.replace("postgresql://", "jdbc:postgresql://");
            }
            
            config.setJdbcUrl(jdbcUrl);
            // Credentials might be in the URL, but Hikari handles that or we can use env
            if (System.getenv("SPRING_DATASOURCE_USERNAME") != null) {
                config.setUsername(System.getenv("SPRING_DATASOURCE_USERNAME"));
            }
            if (System.getenv("SPRING_DATASOURCE_PASSWORD") != null) {
                config.setPassword(System.getenv("SPRING_DATASOURCE_PASSWORD"));
            }
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setInitializationFailTimeout(30000); // Fail after 30s instead of hanging
        config.setConnectionTimeout(30000);
        config.setMaximumPoolSize(5);

        System.out.println("Using JDBC URL: " + config.getJdbcUrl().split("\\?")[0]);
        
        return new HikariDataSource(config);
    }
}

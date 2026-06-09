package com.firstclub.membership.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class ProductionDataSourceConfig {
    @Bean
    DataSource dataSource() {
        String databaseUrl = requireEnvironmentVariable("DATABASE_URL");
        URI uri = URI.create(databaseUrl);
        String[] credentials = uri.getUserInfo().split(":", 2);
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath());
        dataSource.setUsername(credentials[0]);
        dataSource.setPassword(credentials.length > 1 ? credentials[1] : "");
        return dataSource;
    }

    private String requireEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " environment variable is required");
        }
        return value;
    }
}

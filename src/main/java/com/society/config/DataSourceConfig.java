package com.society.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        // 1. Render standard DATABASE_URL or JDBC_DATABASE_URL
        String rawDatabaseUrl = System.getenv("DATABASE_URL");
        if (!StringUtils.hasText(rawDatabaseUrl)) {
            rawDatabaseUrl = System.getenv("JDBC_DATABASE_URL");
        }

        if (StringUtils.hasText(rawDatabaseUrl)) {
            log.info("Configuring DataSource from DATABASE_URL environment variable.");
            configureFromDatabaseUrl(properties, rawDatabaseUrl);
            return properties;
        }

        // 2. Separate SPRING_DATASOURCE_URL
        String springDsUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (StringUtils.hasText(springDsUrl)) {
            log.info("Configuring DataSource from SPRING_DATASOURCE_URL environment variable.");
            configureFromSpringDatasourceUrl(properties, springDsUrl);
            return properties;
        }

        // 3. Fallback for local development (H2 file database)
        log.info("No remote database environment variable detected. Falling back to local H2 file database.");
        properties.setUrl("jdbc:h2:file:./society_db");
        properties.setUsername("sa");
        properties.setPassword("");
        properties.setDriverClassName("org.h2.Driver");
        return properties;
    }

    private void configureFromDatabaseUrl(DataSourceProperties properties, String rawUrl) {
        String url = rawUrl.trim();

        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                // Parse URI by temporarily substituting scheme with http
                String httpUrl = url.replaceFirst("^postgres(ql)?://", "http://");
                URI uri = new URI(httpUrl);

                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // e.g. /nivas_db
                String userInfo = uri.getUserInfo();

                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    properties.setUsername(credentials[0]);
                    properties.setPassword(credentials[1]);
                }

                String query = uri.getQuery();
                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (StringUtils.hasText(query)) {
                    if (!query.contains("sslmode=")) {
                        jdbcUrl += "?" + query + "&sslmode=require";
                    } else {
                        jdbcUrl += "?" + query;
                    }
                } else {
                    jdbcUrl += "?sslmode=require";
                }

                properties.setUrl(jdbcUrl);
                properties.setDriverClassName("org.postgresql.Driver");
                return;
            } catch (URISyntaxException e) {
                log.warn("URI syntax parsing failed for DATABASE_URL; attempting standard string fallback.", e);
                url = url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
            }
        }

        if (url.startsWith("jdbc:postgresql://")) {
            if (!url.contains("sslmode=")) {
                url += (url.contains("?") ? "&" : "?") + "sslmode=require";
            }
            properties.setUrl(url);
            properties.setDriverClassName("org.postgresql.Driver");
        } else {
            properties.setUrl(url);
        }

        String envUsername = System.getenv("SPRING_DATASOURCE_USERNAME");
        String envPassword = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (StringUtils.hasText(envUsername)) {
            properties.setUsername(envUsername);
        }
        if (StringUtils.hasText(envPassword)) {
            properties.setPassword(envPassword);
        }
    }

    private void configureFromSpringDatasourceUrl(DataSourceProperties properties, String springDsUrl) {
        String url = springDsUrl.trim();
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            url = url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
        }

        if (url.startsWith("jdbc:postgresql://")) {
            if (!url.contains("sslmode=")) {
                url += (url.contains("?") ? "&" : "?") + "sslmode=require";
            }
            properties.setDriverClassName("org.postgresql.Driver");
        } else {
            properties.setDriverClassName(System.getenv().getOrDefault("SPRING_DATASOURCE_DRIVER_CLASS_NAME", "org.postgresql.Driver"));
        }

        properties.setUrl(url);
        properties.setUsername(System.getenv("SPRING_DATASOURCE_USERNAME"));
        properties.setPassword(System.getenv("SPRING_DATASOURCE_PASSWORD"));
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}

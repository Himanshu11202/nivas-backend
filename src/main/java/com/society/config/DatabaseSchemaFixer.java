package com.society.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fixes PostgreSQL users_role_check on Render/production where Flyway migrations never ran.
 * Allows SOCIETY_ADMIN and WORKER roles in the users table.
 */
@Component
@Order(1)
public class DatabaseSchemaFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            jdbcTemplate.execute(
                "ALTER TABLE users ADD CONSTRAINT users_role_check " +
                "CHECK (role IN ('RESIDENT', 'ADMIN', 'GUARD', 'SUPER_ADMIN', 'SOCIETY_ADMIN', 'WORKER'))"
            );
            System.out.println("DatabaseSchemaFixer: users_role_check updated (SOCIETY_ADMIN enabled).");
        } catch (Exception e) {
            System.err.println("DatabaseSchemaFixer: could not update users_role_check - " + e.getMessage());
        }
    }
}

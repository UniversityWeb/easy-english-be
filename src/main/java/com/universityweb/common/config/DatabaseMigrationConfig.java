package com.universityweb.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseMigrationConfig {

    private static final Logger log = LogManager.getLogger(DatabaseMigrationConfig.class);

    @Bean
    public CommandLineRunner dropTokenUniqueConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                log.info("Running database migration to drop unique constraint on tokens table...");
                
                // For PostgreSQL, dynamically drop any unique constraint on the tokens table
                String dropQuery = "DO $$\n" +
                        "DECLARE\n" +
                        "    r RECORD;\n" +
                        "BEGIN\n" +
                        "    FOR r IN (\n" +
                        "        SELECT conname \n" +
                        "        FROM pg_constraint \n" +
                        "        WHERE conrelid = 'tokens'::regclass AND contype = 'u'\n" +
                        "    ) LOOP\n" +
                        "        EXECUTE 'ALTER TABLE tokens DROP CONSTRAINT ' || r.conname;\n" +
                        "    END LOOP;\n" +
                        "END;\n" +
                        "$$;";
                jdbcTemplate.execute(dropQuery);
                log.info("Successfully dropped unique constraint on tokens table.");

                // Add missing columns if they do not exist
                log.info("Checking and adding missing columns to tokens table...");
                jdbcTemplate.execute("ALTER TABLE tokens ADD COLUMN IF NOT EXISTS used BOOLEAN DEFAULT FALSE NOT NULL;");
                jdbcTemplate.execute("ALTER TABLE tokens ADD COLUMN IF NOT EXISTS revoked BOOLEAN DEFAULT FALSE NOT NULL;");
                log.info("Successfully checked/added columns 'used' and 'revoked' on tokens table.");
            } catch (Exception e) {
                log.warn("Could not run migration on tokens table (it might not exist or the table is not created yet): {}", e.getMessage());
            }
        };
    }
}

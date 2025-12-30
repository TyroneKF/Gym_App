package com.example.gymapp.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class FlywayMigrationTest
{
    
    private static DataSource dataSource;
    
    @BeforeAll
    static void setupDataSource()
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://localhost:3306/gymapp00001" +
                        "?useSSL=false&allowPublicKeyRetrieval=true"
        );
        config.setUsername("root");
        config.setPassword("password");
        
        dataSource = new HikariDataSource(config);
    }
    
    @BeforeEach
    void cleanAndMigrate()
    {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .cleanDisabled(false)
                .locations(
                        "classpath:db/migration/common",
                        "classpath:db/migration/mysql"
                ).load();
        
        flyway.clean();
        flyway.migrate();
    }
    
    @Test
    void migrationsApplyWithoutError()
    {
        Assertions.assertTrue(true);
    }
}

package com.example.gymapp.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

class FlywaySQLiteMigrationTest
{
    private static DataSource dataSource;
    private static Path dbFile;
    
    @BeforeAll
    static void setupDataSource() throws Exception
    {
        dbFile = Files.createTempFile("gymapp-test", ".db");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.toAbsolutePath());
        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionInitSql("PRAGMA foreign_keys = ON"); // Ensures FK Checks
        
        dataSource = new HikariDataSource(config);
    }
    
    @AfterAll
    static void cleanup() throws Exception
    {
        Files.deleteIfExists(dbFile);
    }
    
    @BeforeEach
    void cleanAndMigrate() throws SQLException
    {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .cleanDisabled(false)
                .locations(
                        "classpath:db/migration/sqlite"
                )
                .load();
        
        flyway.clean();
        flyway.migrate();
        ((HikariDataSource) dataSource).close(); // ensure close
    }
    
    @Test
    void migrationsApplyWithoutError()
    {
        // If we reached here, migrations succeeded
        Assertions.assertTrue(true);
    }
}

package com.fhtechnikum.project.project.Database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for the connection to the PostgreSQL database.
 */

@Slf4j
@Component
public class DatabaseConnector {

    private Connection connection;

    @Autowired
    private DatabaseConfig databaseConfig;

    public void connect(String databaseKey) {
        DatabaseConfig.DataSourceProperties properties = databaseConfig.getDatasources().get(databaseKey);
        if (properties == null) {
            log.error("No datasource configuration found for key: {}", databaseKey);
            return;
        }
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
            log.info("Connected to the PostgreSQL server {} successfully.", databaseKey);
        } catch (Exception e) {
            log.error("Connection to the PostgreSQL server {} failed: {}", databaseKey, e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Disconnected from the PostgreSQL server successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to disconnect from the PostgreSQL server: {}", e.getMessage());
        }
    }

    public ResultSet executeSQLQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            log.error("SQL query execution failed: {}", e.getMessage());
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

package fr.customentity.investment.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    public void buildConfiguration(DatabaseCreditentials creditentials) {
        this.hikariConfig = new HikariConfig();

        this.hikariConfig.setJdbcUrl(creditentials.getConnectionURL());
        this.hikariConfig.setUsername(creditentials.getUsername());
        this.hikariConfig.setPassword(creditentials.getPassword());

        this.hikariConfig.setMaximumPoolSize(10);
        this.hikariConfig.setMaxLifetime(1800000);
        this.hikariConfig.setConnectionTimeout(5000);

        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public HikariConfig getHikariConfig() {
        return hikariConfig;
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }
}

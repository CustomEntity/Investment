package fr.customentity.investment.storage.implementation.sql.dbms.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.customentity.investment.storage.StorageCredentials;
import fr.customentity.investment.storage.implementation.sql.dbms.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConnection implements ConnectionFactory {

    private HikariConfig hikariConfig;
    private HikariDataSource hikariDataSource;

    @Override
    public void init(StorageCredentials credentials) {
        this.hikariConfig = new HikariConfig();

        this.hikariConfig.setJdbcUrl(credentials.getConnectionURL());
        this.hikariConfig.setUsername(credentials.getUsername());
        this.hikariConfig.setPassword(credentials.getPassword());

        this.hikariConfig.setMaximumPoolSize(10);
        this.hikariConfig.setMaxLifetime(1800000);
        this.hikariConfig.setConnectionTimeout(5000);

        this.hikariDataSource = new HikariDataSource(this.hikariConfig);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    @Override
    public String getDbmsName() {
        return "MySQL";
    }



    @Override
    public void shutdown() {

    }
}

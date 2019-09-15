package fr.customentity.investment.sql;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector implements DatabaseConnector {

    private DatabaseCreditentials creditentials;

    public MySQLConnector(DatabaseCreditentials creditentials) {
        this.creditentials = creditentials;
    }

    @Override
    public Connection connect(JavaPlugin javaPlugin) throws SQLException {
        return DriverManager.getConnection(creditentials.getConnectionURL(), creditentials.getUsername(), creditentials.getPassword());
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.jdbc.Driver";
    }
}
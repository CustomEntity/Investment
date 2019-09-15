package fr.customentity.investment.sql;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {

    Connection connect(JavaPlugin javaPlugin) throws SQLException;

    String getName();

    String getDriverClass();

}

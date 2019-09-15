package fr.customentity.investment.sql;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Connector implements DatabaseConnector {

    @Override
    public Connection connect(JavaPlugin javaPlugin) throws SQLException {
        File file = new File(javaPlugin.getDataFolder().getPath(), javaPlugin.getName() + ".mv.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            return DriverManager.getConnection("jdbc:h2:" + file.getAbsolutePath().replace(".mv.db", ""));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getName() {
        return "H2";
    }

    @Override
    public String getDriverClass() {
        return "org.h2.Driver";
    }
}

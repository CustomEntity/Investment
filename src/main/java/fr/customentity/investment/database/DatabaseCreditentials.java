package fr.customentity.investment.database;

import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseCreditentials {
    private String host, database, username, password;
    private int port;

    public DatabaseCreditentials(String host, String database, String username, String password, int port) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getConnectionURL() {
        //TODO: CHANGE URL
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=latin1&useConfigs=maxPerformance";
    }

    public static DatabaseCreditentials fromConfig(JavaPlugin javaPlugin) {
        return new DatabaseCreditentials(javaPlugin.getConfig().getString("mysql.hostname"), javaPlugin.getConfig().getString("mysql.database"),
                javaPlugin.getConfig().getString("mysql.username"), javaPlugin.getConfig().getString("mysql.password"), javaPlugin.getConfig().getInt("mysql.port"));
    }
}

package fr.customentity.investment.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

public class StorageCredentials {

    private final String host;
    private final String database;
    private final String username;
    private final String password;

    public StorageCredentials(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public String getConnectionURL() {
        return Objects.requireNonNull(this.host, "host cannot be null");
    }

    public String getDatabase() {
        return Objects.requireNonNull(this.database, "database cannot be null");
    }

    public String getUsername() {
        return Objects.requireNonNull(this.username, "username cannot be null");
    }

    public String getPassword() {
        return Objects.requireNonNull(this.password, "password cannot be null");
    }

    public static StorageCredentials fromConfig(YamlConfiguration yamlConfiguration) {
        ConfigurationSection storageSection = yamlConfiguration.getConfigurationSection("storage");
        return new StorageCredentials(
                storageSection.getString("hostname"),
                storageSection.getString("database"),
                storageSection.getString("username"),
                storageSection.getString("password")
        );
    }
}

package fr.customentity.investment.configurations;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.locale.Locale;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Singleton
public class MessagesConfig {

    private FileConfiguration messagesConfig;
    private File messagesFile;

    private @Inject InvestmentPlugin plugin;

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        for(Locale locale : Locale.values()) {
            if(!messagesConfig.contains(locale.toString())) {
                messagesConfig.set(locale.toString(), locale.isList() ? locale.getMessage() : locale.getMessage().get(0));
            }
        }
        save();
    }

    public FileConfiguration get() {
        return messagesConfig;
    }

    public void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
        }
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}

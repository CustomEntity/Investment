package fr.customentity.investment.config;

import fr.customentity.investment.Investment;
import fr.customentity.investment.utils.Tl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by CustomEntity on 27/04/2019 for [SpigotMc] Investment.
 */
public class MessagesConfig {

    public FileConfiguration messagesConfig;
    public File messagesFile;

    public void setup() {
        if (!Investment.getInstance().getDataFolder().exists()) {
            Investment.getInstance().getDataFolder().mkdir();
        }

        messagesFile = new File(Investment.getInstance().getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        for(Tl tl : Tl.values()) {
            if(!messagesConfig.contains(tl.toString())) {
                messagesConfig.set(tl.toString(), tl.isList() ? tl.getMessage() : tl.getMessage().get(0));
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

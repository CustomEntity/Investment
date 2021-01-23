package fr.customentity.investment.settings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class Settings {


    private final Set<Setting<?>> settingList = new HashSet<>();
    private final InvestmentPlugin plugin;

    @Inject
    public Settings(InvestmentPlugin plugin) {
        this.plugin = plugin;

        registerSettings();
    }

    public void registerSettings() {
    }

    public void loadSettings() {
        this.settingList.forEach(setting -> setting.load(plugin.getConfig()));
    }

    public void registerSetting(Setting<?> setting) {
        this.settingList.add(setting);
    }


}

package fr.customentity.investment;

import com.google.inject.Guice;
import com.google.inject.Inject;
import fr.customentity.investment.injection.InvestmentModule;
import fr.customentity.investment.injection.PluginModule;
import fr.customentity.investment.managers.InvestmentsManager;
import fr.customentity.investment.settings.Settings;
import org.bukkit.plugin.java.JavaPlugin;

public class InvestmentPlugin extends JavaPlugin {

    private @Inject InvestmentsManager investmentsManager;
    private @Inject Settings settings;


    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Guice.createInjector(new InvestmentModule(), new PluginModule(this));

        this.settings.loadSettings();
    }

    @Override
    public void onDisable() {

    }

    public InvestmentsManager getInvestmentsManager() {
        return investmentsManager;
    }

    public Settings getSettings() {
        return settings;
    }
}

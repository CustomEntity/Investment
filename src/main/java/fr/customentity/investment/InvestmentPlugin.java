package fr.customentity.investment;

import com.google.inject.Guice;
import com.google.inject.Inject;
import fr.customentity.investment.configurations.InvestmentsConfig;
import fr.customentity.investment.gson.GsonManager;
import fr.customentity.investment.injection.InvestmentModule;
import fr.customentity.investment.injection.PluginModule;
import fr.customentity.investment.data.InvestmentsManager;
import fr.customentity.investment.listeners.ListenerManager;
import fr.customentity.investment.settings.Settings;
import org.bukkit.plugin.java.JavaPlugin;

public class InvestmentPlugin extends JavaPlugin {

    private @Inject InvestmentsManager investmentsManager;
    private @Inject ListenerManager listenerManager;
    private @Inject GsonManager gsonManager;

    private @Inject Settings settings;

    private @Inject InvestmentsConfig investmentsConfig;



    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Guice.createInjector(new InvestmentModule(), new PluginModule(this));

        this.listenerManager.registerListeners();

        this.settings.loadSettings();

        this.investmentsConfig.setup();
        this.investmentsConfig.loadInvestments();


    }

    @Override
    public void onDisable() {
        this.investmentsConfig.saveInvestments();
    }


    public GsonManager getGsonManager() {
        return gsonManager;
    }

    public InvestmentsConfig getInvestmentsConfig() {
        return investmentsConfig;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public InvestmentsManager getInvestmentsManager() {
        return investmentsManager;
    }

    public Settings getSettings() {
        return settings;
    }
}

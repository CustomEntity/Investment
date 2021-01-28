package fr.customentity.investment;

import com.google.inject.Guice;
import com.google.inject.Inject;
import fr.customentity.investment.configurations.InvestmentsConfig;
import fr.customentity.investment.configurations.MessagesConfig;
import fr.customentity.investment.gson.GsonManager;
import fr.customentity.investment.injection.InvestmentModule;
import fr.customentity.investment.injection.PluginModule;
import fr.customentity.investment.data.InvestmentsManager;
import fr.customentity.investment.listeners.ListenerManager;
import fr.customentity.investment.settings.Settings;
import fr.customentity.investment.tasks.InvestmentTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class InvestmentPlugin extends JavaPlugin {

    private @Inject InvestmentsManager investmentsManager;
    private @Inject ListenerManager listenerManager;
    private @Inject GsonManager gsonManager;

    private @Inject Settings settings;

    private @Inject InvestmentsConfig investmentsConfig;
    private @Inject MessagesConfig messagesConfig;

    private @Inject InvestmentTask investmentTask;

    private Economy economy;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Guice.createInjector(new InvestmentModule(), new PluginModule(this));

        if(!this.setupEconomy()) {
            this.getLogger().log(Level.WARNING, "Vault provider not found ! Disabling the plugin..");
        }

        this.listenerManager.registerListeners();

        this.settings.loadSettings();

        this.investmentsConfig.setup();
        this.investmentsConfig.loadInvestments();

        this.messagesConfig.setup();

        this.investmentTask.runTaskTimer(this, 20, 20);
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

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public Settings getSettings() {
        return settings;
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}

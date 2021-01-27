package fr.customentity.investment.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import org.bukkit.Bukkit;

@Singleton
public class ListenerManager{

    private @Inject InvestmentPlugin plugin;
    private @Inject PlayerListener playerListener;

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(playerListener, plugin);
    }
}

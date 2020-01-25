package fr.customentity.investment.hooks;

import fr.customentity.investment.Investment;
import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * Created by CustomEntity on 1/25/2020 for [SpigotMc] Investment.
 */
public class HooksManager {

    private Hook currentaAfkHook;

    public void setupHooks() {
        setupAfkHook();
    }

    private void setupAfkHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("AntiAFKPlus")) {
            currentaAfkHook = new AntiAfkPlusHook();
            currentaAfkHook.setup();
            Bukkit.getPluginManager().registerEvents(new AntiAfkPlusHook(), Investment.getInstance());
            Investment.getInstance().getLogger().log(Level.INFO, "AntiAFKPlus hooked !");
        } else if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            currentaAfkHook = new EssentialHook();
            currentaAfkHook.setup();
            Bukkit.getPluginManager().registerEvents(new EssentialHook(), Investment.getInstance());
            Investment.getInstance().getLogger().log(Level.INFO, "Essentials hooked !");
        }
    }
}

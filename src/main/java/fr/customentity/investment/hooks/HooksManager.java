package fr.customentity.investment.hooks;

import fr.customentity.investment.Investment;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by CustomEntity on 1/25/2020 for [SpigotMc] Investment.
 */
public class HooksManager {

    private List<Hook> hookList = new ArrayList<>();

    public void setupHooks() {
        setupAfkHook();
    }

    private void setupAfkHook() {
        if (Bukkit.getPluginManager().isPluginEnabled("AntiAFKPlus")) {
            AntiAfkPlusHook antiAfkPlusHook = new AntiAfkPlusHook();
            hookList.add(antiAfkPlusHook);
            antiAfkPlusHook.setup();
            Bukkit.getPluginManager().registerEvents(new AntiAfkPlusHook(), Investment.getInstance());
            Investment.getInstance().getLogger().log(Level.INFO, "AntiAFKPlus hooked !");
        } else if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            EssentialHook essentialHook = new EssentialHook();
            hookList.add(essentialHook);
            essentialHook.setup();
            Bukkit.getPluginManager().registerEvents(new EssentialHook(), Investment.getInstance());
            Investment.getInstance().getLogger().log(Level.INFO, "Essentials hooked !");
        }
    }

    public List<Hook> getHooks() {
        return hookList;
    }
}

package fr.customentity.investment.hook.all;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import fr.customentity.investment.hook.WorldEditSelection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by CustomEntity on 02/05/2019 for [SpigotMc] Investment.
 */
public class WorldEdit_6_1_9 implements WorldEditSelection {

    private final WorldEditPlugin plugin;

    public WorldEdit_6_1_9(JavaPlugin javaPlugin) {
        this.plugin = (WorldEditPlugin) javaPlugin;
    }

    @Override
    public Location getMinimumPoint(Player player) {
        return plugin.getSelection(player).getMinimumPoint();
    }

    @Override
    public Location getMaximumPoint(Player player) {
        return plugin.getSelection(player).getMaximumPoint();
    }
}

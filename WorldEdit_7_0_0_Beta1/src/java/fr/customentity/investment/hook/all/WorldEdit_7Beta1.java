package fr.customentity.investment.hook.all;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import fr.customentity.investment.hook.WorldEditSelection;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by CustomEntity on 02/05/2019 for [SpigotMc] Investment.
 */
public class WorldEdit_7Beta1 implements WorldEditSelection {

    private final WorldEditPlugin plugin;

    public WorldEdit_7Beta1(JavaPlugin javaPlugin) {
        this.plugin = (WorldEditPlugin) javaPlugin;
    }

    @Override
    public Location getMinimumPoint(Player player) {
        Location location = null;
        try {
            Vector vector = plugin.getSession(player).getSelection(plugin.getSession(player).getSelectionWorld()).getMinimumPoint();
            location = new Location(player.getWorld(), vector.getBlockX(), vector.getBlockY(),vector.getBlockZ());
        } catch (IncompleteRegionException e) {
        }
        return location;
    }

    @Override
    public Location getMaximumPoint(Player player) {
        Location location = null;
        try {
            Vector vector = plugin.getSession(player).getSelection(plugin.getSession(player).getSelectionWorld()).getMaximumPoint();
            location = new Location(player.getWorld(), vector.getBlockX(), vector.getBlockY(),vector.getBlockZ());
        } catch (IncompleteRegionException e) {
        }
        return location;
    }

}

package fr.customentity.investment.utils;

import fr.customentity.investment.exceptions.WorldDoesntExistException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by CustomEntity on 1/24/2020 for [SpigotMc] Investment.
 */
public class SerializationUtils {

    public static Location deserializeLocation(String string) throws WorldDoesntExistException {
        if (string.equalsIgnoreCase("null")) {
            return null;
        }
        String[] locString = string.split(":");
        World world = Bukkit.getWorld(locString[0]);
        if(world == null)throw new WorldDoesntExistException();
        return new Location(world, Double.parseDouble(locString[1]),
                Double.parseDouble(locString[2]), Double.parseDouble(locString[3]));
    }

    public static String serializeLocation(Location location) {
        if (location == null) {
            return "NoLocFound";
        }
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
    }
}

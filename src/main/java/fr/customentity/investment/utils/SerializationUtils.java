package fr.customentity.investment.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by CustomEntity on 1/24/2020 for [SpigotMc] Investment.
 */
public class SerializationUtils {

    public static Location deserializeLocation(String string)  {
        if (string == null || string.equalsIgnoreCase("null") || string.split(":").length == 0) {
            return null;
        }
        String[] locString = string.split(":");
        World world = Bukkit.getWorld(locString[0]);
        if(world == null)return null;
        return new Location(world, Double.parseDouble(locString[1]),
                Double.parseDouble(locString[2]), Double.parseDouble(locString[3]));
    }

    public static String serializeLocation(Location location) {
        if (location == null) {
            return "null";
        }
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
    }
}

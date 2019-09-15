package fr.customentity.investment.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldEditSelection {
    Location getMinimumPoint(Player player);
    Location getMaximumPoint(Player player);
}

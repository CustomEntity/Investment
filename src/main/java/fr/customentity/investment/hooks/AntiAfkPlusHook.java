package fr.customentity.investment.hooks;

import de.kinglol12345.AntiAFKPlus.AFKPlayer;
import de.kinglol12345.AntiAFKPlus.api.AntiAFPPlusAPIImpl;
import de.kinglol12345.AntiAFKPlus.events.AfkStatusChangedEvent;
import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.exceptions.WorldDoesntExistException;
import fr.customentity.investment.utils.SerializationUtils;
import fr.customentity.investment.utils.Tl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

/**
 * Created by CustomEntity on 11/22/2019 for [SpigotMc] Investment.
 */
public class AntiAfkPlusHook implements Hook, Listener {

    @Override
    public void setup() {
    }

    @EventHandler
    public void onAfkStatutChange(AfkStatusChangedEvent event) {
        Player player = event.getPlayer();
        if (event.isAfk()) {
            if (Investment.getInstance().getInvestmentZone().isIn(player) && Investment.getInstance().getConfig().getBoolean("settings.afk.anti-kick-player")) {
                AFKPlayer.all.remove(player);
            } else {
                if(Investment.getInstance().getConfig().getBoolean("settings.afk.teleport-when-afk")) {
                    Location loc = null;
                    try {
                        InvestPlayer.wrap(player).setOriginalLocation(player.getLocation());
                        loc = SerializationUtils.deserializeLocation(Investment.getInstance().getConfig().getString("settings.afk.afk-teleport-location"));
                        player.teleport(loc);
                        Tl.sendConfigMessage(player, Tl.INVESTMENT_MOVE$TO$INVESTMENT$ZONE);
                    } catch (WorldDoesntExistException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}

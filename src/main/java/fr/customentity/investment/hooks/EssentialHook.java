package fr.customentity.investment.hooks;

import com.earth2me.essentials.Essentials;
import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.utils.SerializationUtils;
import fr.customentity.investment.utils.Tl;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by CustomEntity on 11/22/2019 for [SpigotMc] Investment.
 */
public class EssentialHook implements Hook, Listener {

    Essentials essentials;

    @Override
    public void setup() {
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @EventHandler
    public void onAfkStateChange(AfkStatusChangeEvent event) {
        Player player = event.getAffected().getBase();
        if (event.getValue()) {
            if (Investment.getInstance().getInvestmentZone().isIn(player) && Investment.getInstance().getConfig().getBoolean("settings.afk.anti-kick-player")) {
                event.getAffected().setAfkMessage(null);
                event.setCancelled(true);
            } else {
                if(Investment.getInstance().getConfig().getBoolean("settings.afk.teleport-when-afk")) {
                    Location loc = null;
                        InvestPlayer.wrap(player).setOriginalLocation(player.getLocation());
                        loc = SerializationUtils.deserializeLocation(Investment.getInstance().getConfig().getString("settings.afk.afk-teleport-location"));
                        if(loc != null) {
                            player.teleport(loc);
                            Tl.sendConfigMessage(player, Tl.INVESTMENT_MOVE$TO$INVESTMENT$ZONE);
                        }

                }
            }
        }
    }
}

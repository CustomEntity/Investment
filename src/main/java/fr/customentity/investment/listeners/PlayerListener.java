package fr.customentity.investment.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.data.player.InvestPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Optional;

@Singleton
public class PlayerListener implements Listener {

    private final InvestmentPlugin plugin;
    private final InvestPlayer.InvestPlayerFactory playerFactory;

    @Inject
    public PlayerListener(InvestmentPlugin plugin,
                          InvestPlayer.InvestPlayerFactory playerFactory) {
        this.plugin = plugin;
        this.playerFactory = playerFactory;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        InvestPlayer investPlayer = this.playerFactory.create(player);
        plugin.getInvestPlayerManager().getInvestPlayers().put(player.getUniqueId(), investPlayer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<InvestPlayer> investPlayer = this.plugin.getInvestPlayerManager().wrapPlayer(player);
        investPlayer.ifPresent(InvestPlayer::destroy);
    }

}

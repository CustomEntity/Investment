package fr.customentity.investment.data.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import fr.customentity.investment.InvestmentPlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InvestPlayer {

    private transient InvestmentPlugin plugin;

    private UUID uuid;

    public interface InvestPlayerFactory {
        InvestPlayer create(Player player);
    }

    @Inject
    public InvestPlayer(InvestmentPlugin plugin, @Assisted Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
    }

    public void destroy() {
        //TODO: DESTROY
        this.plugin.getInvestPlayerManager().getInvestPlayers().remove(uuid);
    }
}

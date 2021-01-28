package fr.customentity.investment.data.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class InvestPlayerManager {

    private final InvestmentPlugin plugin;

    private final HashMap<UUID, InvestPlayer> investPlayers;

    @Inject
    public InvestPlayerManager(InvestmentPlugin plugin) {
        this.plugin = plugin;
        this.investPlayers = new HashMap<>();
    }

    public HashMap<UUID, InvestPlayer> getInvestPlayers() {
        return investPlayers;
    }

    public Optional<InvestPlayer> wrapPlayer(Player player) {
        return this.wrapPlayer(player.getUniqueId());
    }

    public Optional<InvestPlayer> wrapPlayer(UUID uuid) {
        return Optional.ofNullable(investPlayers.get(uuid));
    }
}

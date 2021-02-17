package fr.customentity.investment.data.player;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.data.investments.Investment;
import fr.customentity.investment.data.investments.InvestmentProgression;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class InvestPlayer {

    private transient InvestmentPlugin plugin;

    private UUID uuid;
    private InvestmentProgression investmentProgression;
    
    private transient boolean isInvesting;

    public interface InvestPlayerFactory {
        InvestPlayer create(Player player);
    }

    @Inject
    public InvestPlayer(InvestmentPlugin plugin,
                        @Assisted Player player) {
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Optional<InvestmentProgression> getInvestmentProgression() {
        return Optional.ofNullable(investmentProgression);
    }

    public void setInvesting(boolean investing) {
        isInvesting = investing;
    }

    public void setInvestmentProgression(InvestmentProgression investmentProgression) {
        this.investmentProgression = investmentProgression;
    }

    public boolean isInvesting() {
        return isInvesting;
    }

    public void destroy() {
        //TODO: SAVE DATAS
        this.plugin.getInvestPlayerManager().getInvestPlayers().remove(uuid);
    }
}

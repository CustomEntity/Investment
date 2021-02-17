package fr.customentity.investment.storage.implementation;

import fr.customentity.investment.data.investments.Investment;
import fr.customentity.investment.data.investments.InvestmentProgression;
import fr.customentity.investment.storage.StorageCredentials;
import org.bukkit.entity.Player;

public interface StorageImplementation {

    void init(StorageCredentials credentials) throws Exception;

    void shutdown();

    String getImplementationName();

    boolean hasInvestment(Player player);

    void setInvestment(Player player, Investment investment);

    InvestmentProgression getInvestment(Player player);
}

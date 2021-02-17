package fr.customentity.investment.storage.implementation.sql;

import fr.customentity.investment.data.investments.Investment;
import fr.customentity.investment.data.investments.InvestmentProgression;
import fr.customentity.investment.storage.StorageCredentials;
import fr.customentity.investment.storage.implementation.StorageImplementation;
import org.bukkit.entity.Player;

public class SqlStorage implements StorageImplementation {


    @Override
    public void init(StorageCredentials storageCredentials) throws Exception {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String getImplementationName() {
        return null;
    }

    @Override
    public boolean hasInvestment(Player player) {
        return false;
    }

    @Override
    public void setInvestment(Player player, Investment investment) {

    }

    @Override
    public InvestmentProgression getInvestment(Player player) {
        return null;
    }
}

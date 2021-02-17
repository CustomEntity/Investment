package fr.customentity.investment.storage;

import com.google.inject.Inject;
import fr.customentity.investment.InvestmentPlugin;

public class StorageManager {

    private InvestmentPlugin plugin;

    @Inject
    public StorageManager(InvestmentPlugin plugin) {
        this.plugin = plugin;
    }


    public void init() {
    }
}

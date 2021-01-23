package fr.customentity.investment.managers;

import fr.customentity.investment.InvestmentPlugin;

public abstract class Manager {

    private final InvestmentPlugin plugin;

    public Manager(InvestmentPlugin plugin) {
        this.plugin = plugin;
    }
}

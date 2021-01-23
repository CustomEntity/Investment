package fr.customentity.investment.managers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;

@Singleton
public class InvestmentsManager extends Manager{

    @Inject
    public InvestmentsManager(InvestmentPlugin plugin) {
        super(plugin);
    }
}

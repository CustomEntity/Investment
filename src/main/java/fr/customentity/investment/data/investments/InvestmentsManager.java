package fr.customentity.investment.data.investments;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class InvestmentsManager {

    private final InvestmentPlugin plugin;
    private Set<Investment> investmentSet;

    @Inject
    public InvestmentsManager(InvestmentPlugin plugin) {
        this.plugin = plugin;
        this.investmentSet = new HashSet<>();
    }

    public void setInvestmentSet(Set<Investment> investmentSet) {
        this.investmentSet = investmentSet;
    }

    public Set<Investment> getInvestmentSet() {
        return investmentSet;
    }
}

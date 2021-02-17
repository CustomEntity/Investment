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

    private Investment.Factory investmentFactory;

    @Inject
    public InvestmentsManager(InvestmentPlugin plugin, Investment.Factory investmentFactory) {
        this.plugin = plugin;
        this.investmentSet = new HashSet<>();
        this.investmentFactory = investmentFactory;
    }

    public void setInvestmentSet(Set<Investment> investmentSet) {
        this.investmentSet = investmentSet;
    }

    public Set<Investment> getInvestmentSet() {
        return investmentSet;
    }


    public void createInvestment(Investment.Type type, String name, double amountToInvest, double reward, int timeToStay) {
        this.investmentSet.add(investmentFactory.create(type, name, amountToInvest, reward, timeToStay));
    }
}

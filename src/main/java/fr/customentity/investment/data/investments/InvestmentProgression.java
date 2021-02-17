package fr.customentity.investment.data.investments;

public class InvestmentProgression {

    private Investment investment;
    private int timeInvested;

    public InvestmentProgression(Investment investment) {
        this.investment = investment;
        this.timeInvested = 0;
    }

    public int getTimeInvested() {
        return timeInvested;
    }

    public Investment getInvestment() {
        return investment;
    }
}

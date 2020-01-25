package fr.customentity.investment.data;

/**
 * Created by CustomEntity on 1/24/2020 for [SpigotMc] Investment.
 */
public enum InvestmentType {

    MONEY("Money"),
    EXPERIENCE("Experience");

    private String name;

    InvestmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

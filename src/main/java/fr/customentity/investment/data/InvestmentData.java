package fr.customentity.investment.data;

import fr.customentity.investment.Investment;
import fr.customentity.investment.utils.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CustomEntity on 07/03/2019 for [SpigotMc] Investment.
 */
public class InvestmentData {

    public static List<InvestmentData> investmentDataList = new ArrayList<>();

    private String name;
    private long toInvest;
    private int timeToStay;
    private long reward;

    public InvestmentData(String name, int timeToStay, long toInvest, long reward) {
        this.name = name;
        this.toInvest = toInvest;
        this.timeToStay = timeToStay;
        this.reward = reward;
    }

    public int getTimeToStay() {
        return timeToStay;
    }

    public long getReward() {
        return reward;
    }

    public String getName() {
        return name;
    }

    public long getToInvest() {
        return toInvest;
    }

    public static InvestmentData getInvestmentDataByName(String name) {
        for (InvestmentData investmentData : investmentDataList) {
            if (investmentData.getName().equalsIgnoreCase(name)) {
                return investmentData;
            }
        }
        return null;
    }

    public static List<InvestmentData> getInvestments() {
        return investmentDataList;
    }

    public static void deleteInvestment(InvestmentData investmentData) {
        if (investmentData != null) {
            for (Player pls : Bukkit.getOnlinePlayers()) {
                InvestPlayer investPlayer = InvestPlayer.wrap(pls);
                if (investPlayer.hasInvestment() && investPlayer.getCurrentInvestment().getName().equalsIgnoreCase(investmentData.getName())) {
                    investPlayer.setInvestmentData(null);
                    investPlayer.setTimeStayed(0);
                }
            }
            Investment.getInstance().getInvestmentConfig().get().set("investments." + investmentData.getName(), null);
            Investment.getInstance().getDatabaseSQL().deleteAllInvestment(investmentData);
            investmentDataList.remove(investmentData);
        }
    }

    public static void createInvestment(String name, int timeToStay, long toInvest, long reward) {
        investmentDataList.add(new InvestmentData(name, timeToStay, toInvest, reward));
        Investment.getInstance().getInvestmentConfig().get().createSection("investments." + name);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".timeToStay", timeToStay);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".toInvest", toInvest);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".reward", reward);

        Investment.getInstance().getInvestmentConfig().save();
    }
}

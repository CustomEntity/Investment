package fr.customentity.investment.data;

import fr.customentity.investment.Investment;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CustomEntity on 07/03/2019 for [SpigotMc] Investment.
 */
public class InvestPlayer {

    public static List<InvestPlayer> investPlayerList = new ArrayList<>();
    private Player player;
    private InvestmentData investmentData;
    private int timeStayed;
    private int timeStayedToday;
    private Location originalLocation;

    public InvestPlayer(Player player) {
        this.player = player;
        this.timeStayed = 0;
        this.timeStayedToday = 0;

        if (Investment.getInstance().getDatabaseSQL().hasAccount(player)) {
            Investment.getInstance().getDatabaseSQL().loadData(this);
        } else {
            Investment.getInstance().getDatabaseSQL().createAccount(player);
        }
        investPlayerList.add(this);
    }

    public Location getOriginalLocation() {
        return originalLocation;
    }

    public void setOriginalLocation(Location originalLocation) {
        this.originalLocation = originalLocation;
    }

    public Player getPlayer() {
        return player;
    }

    public void setInvestmentData(InvestmentData investmentData) {
        this.investmentData = investmentData;
    }

    public void setTimeStayed(int timeStayed) {
        this.timeStayed = timeStayed;
    }

    public int getTimeStayed() {
        return timeStayed;
    }

    public InvestmentData getCurrentInvestment() {
        return investmentData;
    }

    public void destroy() {
        Investment.getInstance().getDatabaseSQL().saveData(this);
        investPlayerList.remove(this);
    }

    public void finishCurrentInvestment() {
        investmentData.finishInvestment(this);
    }

    public void resetInvestment() {
        this.setInvestmentData(null);
        this.setTimeStayed(0);
    }

    public boolean hasInvestment() {
        return investmentData != null;
    }

    public void setTimeStayedToday(int timeStayedToday) {
        this.timeStayedToday = timeStayedToday;
    }

    public int getTimeStayedToday() {
        return timeStayedToday;
    }

    public static InvestPlayer wrap(Player player) {
        for (InvestPlayer investPlayer : investPlayerList) {
            if (investPlayer.getPlayer().equals(player)) {
                return investPlayer;
            }
        }
        return new InvestPlayer(player);
    }
}

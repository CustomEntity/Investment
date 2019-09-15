package fr.customentity.investment.data;

import fr.customentity.investment.Investment;
import fr.customentity.investment.utils.MoneyFormat;
import fr.customentity.investment.utils.Tl;
import org.bukkit.Bukkit;
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

    public InvestPlayer(Player player) {
        this.player = player;
        this.timeStayed = 0;
        this.timeStayedToday = 0;

        if(Investment.getInstance().getDatabaseSQL().hasAccount(player)) {
            Investment.getInstance().getDatabaseSQL().loadData(this);
        } else {
            Investment.getInstance().getDatabaseSQL().createAccount(player);
        }
        investPlayerList.add(this);
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

    public boolean hasInvestment() {
        return investmentData != null;
    }

    public void setTimeStayedToday(int timeStayedToday) {
        this.timeStayedToday = timeStayedToday;
    }

    public int getTimeStayedToday() {
        return timeStayedToday;
    }

    public void startInvestment(InvestmentData investmentData) {
        boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
        if (Investment.getInstance().getEcon().has(player, investmentData.getToInvest())) {
            Investment.getInstance().getEcon().withdrawPlayer(player, investmentData.getToInvest());
            setInvestmentData(investmentData);
            setTimeStayed(0);
            Tl.sendConfigMessage(player, Tl.ON_INVEST, "%reward%", moneyFormat ? MoneyFormat.format(investmentData.getReward()) + "": investmentData.getReward() + "", "%invested%", moneyFormat ? MoneyFormat.format(investmentData.getToInvest()) + "": investmentData.getToInvest() + "", "%investment%", investmentData.getName());
            Investment.getInstance().getDatabaseSQL().setInvestment(player, investmentData);
        } else {

            Tl.sendConfigMessage(player, Tl.NO_ENOUGH_MONEY, "%reward%", moneyFormat ? MoneyFormat.format(investmentData.getReward()) + "": investmentData.getReward() + "", "%invested%", moneyFormat ? MoneyFormat.format(investmentData.getToInvest()) + "": investmentData.getToInvest() + "", "%investment%", investmentData.getName());
        }
    }

    public void finishInvestment() {
        InvestmentData currentInvestment = getCurrentInvestment();
        Investment.getInstance().getEcon().depositPlayer(player, currentInvestment.getReward());
        boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
        Tl.sendConfigMessage(player, Tl.ON_INVEST_FINISH, "%reward%", moneyFormat ? MoneyFormat.format(investmentData.getReward()) + "": investmentData.getReward() + "", "%invested%", moneyFormat ? MoneyFormat.format(investmentData.getToInvest()) + "": investmentData.getToInvest() + "", "%investment%", investmentData.getName());
        Investment.getInstance().getDatabaseSQL().removeInvestment(player);
        Bukkit.getScheduler().runTask(Investment.getInstance(), () -> {
            for(String commands : Investment.getInstance().getConfig().getStringList("settings.commands-when-player-finish-investment")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("%displayname%", player.getDisplayName()).replace("%reward%", investmentData.getReward() + "").replace("%invested%", investmentData.getToInvest() + "").replace("%investment%", investmentData.getName()).replace("%player%", player.getName()));
            }
        });
        this.investmentData = null;
        setTimeStayed(0);
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

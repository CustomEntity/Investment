package fr.customentity.investment.data;

import fr.customentity.investment.Investment;
import fr.customentity.investment.utils.CurrencyFormat;
import fr.customentity.investment.utils.Tl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    private InvestmentType investmentType;
    private long reward;
    private List<String> commandsToExecute;

    public InvestmentData(String name, int timeToStay, long toInvest, long reward, InvestmentType investmentType, List<String> commandsToExecute) {
        this.name = name;
        this.toInvest = toInvest;
        this.timeToStay = timeToStay;
        this.reward = reward;
        this.investmentType = investmentType;

        this.commandsToExecute = commandsToExecute;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public int getTimeToStay() {
        return timeToStay;
    }

    public List<String> getCommandsToExecute() {
        return commandsToExecute;
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

    private boolean hasEnough(Player player) {
        if(investmentType == InvestmentType.MONEY) {
            return Investment.getInstance().getEcon().has(player, this.toInvest);
        } else {
            return player.getTotalExperience() >= this.toInvest;
        }
    }

    public void withdraw(Player player) {
        if(investmentType == InvestmentType.MONEY) {
            Investment.getInstance().getEcon().withdrawPlayer(player, this.toInvest);
        } else {
            player.setTotalExperience((int) (player.getTotalExperience() - this.toInvest));
        }
    }

    public void deposit(Player player) {
        if(investmentType == InvestmentType.MONEY) {
            Investment.getInstance().getEcon().depositPlayer(player, this.toInvest);
        } else {
            player.setTotalExperience((int) (player.getTotalExperience() + this.toInvest));
        }
    }

    public void deposit(Player player, long amount) {
        if(investmentType == InvestmentType.MONEY) {
            Investment.getInstance().getEcon().depositPlayer(player, amount);
        } else {
            player.setTotalExperience((int) (player.getTotalExperience() + amount));
        }
    }


    public void startInvestment(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
        if (hasEnough(player)) {
            withdraw(player);
            investPlayer.setInvestmentData(this);
            investPlayer.setTimeStayed(0);
            Tl.sendConfigMessage(investPlayer.getPlayer(), getInvestmentType() == InvestmentType.MONEY ? Tl.INVESTMENT_ON$INVEST_MONEY : Tl.INVESTMENT_ON$INVEST_EXPERIENCE, "%reward%", moneyFormat ? CurrencyFormat.format(this.reward) + "" : this.reward + "", "%invested%", moneyFormat ? CurrencyFormat.format(this.toInvest) + "" : this.toInvest + "", "%investment%", this.name);
            Investment.getInstance().getDatabaseSQL().setInvestment(player, this);
        } else {
            Tl.sendConfigMessage(player, getInvestmentType() == InvestmentType.MONEY ? Tl.INVESTMENT_NO$ENOUGH_MONEY : Tl.INVESTMENT_NO$ENOUGH_EXPERIENCE, "%reward%", moneyFormat ? CurrencyFormat.format(this.reward) + "" : this.reward + "", "%invested%", moneyFormat ? CurrencyFormat.format(this.toInvest) + "" : this.toInvest + "", "%investment%", this.name);
        }
    }

    public void finishInvestment(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        deposit(player);
        boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
        Tl.sendConfigMessage(player, getInvestmentType() == InvestmentType.MONEY ? Tl.INVESTMENT_ON$FINISH_MONEY : Tl.INVESTMENT_ON$FINISH_EXPERIENCE, "%reward%", moneyFormat ? CurrencyFormat.format(this.reward) + "" : this.reward + "", "%invested%", moneyFormat ? CurrencyFormat.format(this.toInvest) + "" : this.toInvest + "", "%investment%", this.name);
        Investment.getInstance().getDatabaseSQL().removeInvestment(player);
        Bukkit.getScheduler().runTask(Investment.getInstance(), () -> {
            if (Investment.getInstance().getConfig().contains("settings.commands-when-player-finish-investment")) {
                for (String commands : Investment.getInstance().getConfig().getStringList("settings.commands-when-player-finish-investment")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("%displayname%", player.getDisplayName()).replace("%reward%", this.reward + "").replace("%invested%", this.toInvest + "").replace("%investment%", this.name).replace("%player%", player.getName()));
                }
            }
            this.commandsToExecute.forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%displayname%", player.getDisplayName()).replace("%player%", player.getName())));
        });
        investPlayer.resetInvestment();
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

    public static void createInvestment(String name, int timeToStay, long toInvest, long reward, InvestmentType investmentType, List<String> commandsToExecute) {
        investmentDataList.add(new InvestmentData(name, timeToStay, toInvest, reward, investmentType, commandsToExecute));
        Investment.getInstance().getInvestmentConfig().get().createSection("investments." + name);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".timeToStay", timeToStay);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".toInvest", toInvest);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".reward", reward);
        Investment.getInstance().getInvestmentConfig().get().set("investments." + name + ".commandsToExecute", commandsToExecute);

        Investment.getInstance().getInvestmentConfig().save();
    }
}

package fr.customentity.investment.config;

import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class InvestmentConfig {

    public FileConfiguration investmentConfig;
    public File investmentFile;

    public void setup() {
        if (!Investment.getInstance().getDataFolder().exists()) {
            Investment.getInstance().getDataFolder().mkdir();
        }

        investmentFile = new File(Investment.getInstance().getDataFolder(), "investments.yml");
        if(!investmentFile.exists()) {
            try {
                investmentFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        investmentConfig = YamlConfiguration.loadConfiguration(investmentFile);
        if(!investmentConfig.contains("investments")) {
            investmentConfig.createSection("investments");
            investmentConfig.createSection("investments.investment1");
            investmentConfig.set("investments.investment1.timeToStay", 1800);
            investmentConfig.set("investments.investment1.toInvest", 10000);
            investmentConfig.set("investments.investment1.reward", 25000);
        }
        save();
    }

    public FileConfiguration get() {
        return investmentConfig;
    }

    public void save() {
        try {
            investmentConfig.save(investmentFile);
        } catch (IOException e) {
        }
    }

    public void reload() {
        investmentConfig = YamlConfiguration.loadConfiguration(investmentFile);

        InvestmentData.investmentDataList.clear();
        loadInvestments();
    }

    public void loadInvestments() {
        for (String str : get().getConfigurationSection("investments").getKeys(false)) {
            int timeToStay = get().getInt("investments." + str + ".timeToStay");
            long toInvest = (long) get().getDouble("investments." + str + ".toInvest");
            long reward = (long) get().getDouble("investments." + str + ".reward");

            InvestmentData.createInvestment(str, timeToStay, toInvest, reward);
        }
    }

    public void saveInvestments() {
        for (InvestmentData investmentData : InvestmentData.getInvestments()) {
            get().set("investments." + investmentData.getName(), null);
            get().set("investments." + investmentData.getName() + ".timeToStay", investmentData.getTimeToStay());
            get().set("investments." + investmentData.getName() + ".toInvest", investmentData.getToInvest());
            get().set("investments." + investmentData.getName() + ".reward", investmentData.getReward());
        }
        save();
    }

}

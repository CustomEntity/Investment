package fr.customentity.investment.config;

import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.utils.MoneyFormat;
import fr.customentity.investment.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by JSONArray on 08/06/2018 for Duel.
 */
public class InvestmentGuiConfig {

    public FileConfiguration investmentConfig;
    public File investmentFile;

    public void setup() {
        if (!Investment.getInstance().getDataFolder().exists()) {
            Investment.getInstance().getDataFolder().mkdir();
        }

        investmentFile = new File(Investment.getInstance().getDataFolder(), "gui.yml");
        if (!investmentFile.exists()) {
            Investment.getInstance().saveResource("gui.yml", false);
        }
        investmentConfig = YamlConfiguration.loadConfiguration(investmentFile);
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
    }


    public FileConfiguration getConfig() {
        return investmentConfig;
    }

    public ItemStack getItemConfig(String path) {
        if(!get().contains(path + ".id") && !get().contains(path + ".data"))return null;
        if (get().contains(path + ".id")) {
            Investment.getInstance().getLogger().log(Level.WARNING, "Please replace 'id' to 'type' in the gui.yml. Example: id:399 to type:NETHER_STAR");
            return new ItemStack(Material.STONE);
        }
        int data = 0;
        if (getConfig().contains(path + ".data")) {
            data = getConfig().getInt(path + ".data");
        }
        XMaterial xMaterial;
        if(data == 0) {
            xMaterial = XMaterial.matchXMaterial(getConfig().getString(path + ".type"));
        } else {
            xMaterial = XMaterial.matchXMaterial(getConfig().getString(path + ".type"), (byte)data);
        }
        if (xMaterial == null) {
            return new ItemStack(Material.STONE);
        }
        int amount = 1;
        if (getConfig().contains(path + ".amount")) {
            amount = getConfig().getInt(path + ".amount");
        }
        ItemStack itemStack = xMaterial.parseItem();
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString(path + ".name")));
        if (getConfig().contains(path + ".lore")) {
            itemMeta.setLore(Investment.getInstance().translateColorList(getConfig().getStringList(path + ".lore")));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, get().getInt("size"), ChatColor.translateAlternateColorCodes('&', get().getString("name")));

        ItemStack idle = getItemConfig("items.idle-items");

        if(idle == null) {
            if (get().getConfigurationSection("items.idle-items") != null && get().getConfigurationSection("items.idle-items").getKeys(false).size() != 0) {
                for (String str : get().getConfigurationSection("items.idle-items").getKeys(false)) {
                    ItemStack it = getItemConfig("items.idle-items." + str);
                    for (String slot : get().getStringList("items.idle-items." + str + ".slots")) {
                        inventory.setItem(Integer.parseInt(slot), it);
                    }
                }
            }
        } else {
            for(String slot : get().getStringList("items.idle-items.idle-items-slots")) {
                inventory.setItem(Integer.parseInt(slot), idle);
            }
        }

        if (get().getConfigurationSection("items.other-items") != null && get().getConfigurationSection("items.other-items").getKeys(false).size() != 0) {
            for (String others : get().getConfigurationSection("items.other-items").getKeys(false)) {
                ItemStack it = getItemConfig("items.other-items." + others);
                inventory.setItem(get().getInt("items.other-items." + others + ".slot"), it);
            }
        }
        if (get().getConfigurationSection("items.investments-items").getKeys(false).size() != 0) {
            for (String investments : get().getConfigurationSection("items.investments-items").getKeys(false)) {
                InvestmentData investmentData = InvestmentData.getInvestmentDataByName(investments);
                if(investmentData == null)continue;
                boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
                ItemStack it = getItemConfig("items.investments-items." + investments);
                ItemMeta im = it.getItemMeta();
                im.setDisplayName(im.getDisplayName().replace("%reward%", moneyFormat ? MoneyFormat.format(investmentData.getReward()) + "": investmentData.getReward() + "").replace("%toInvest%", moneyFormat ? MoneyFormat.format(investmentData.getToInvest()) + "": investmentData.getToInvest() + ""));
                List<String> translatedLore = new ArrayList<>();
                im.getLore().forEach(s -> translatedLore.add(s.replace("%reward%", moneyFormat ? MoneyFormat.format(investmentData.getReward()) + "": investmentData.getReward() + "").replace("%toInvest%", moneyFormat ? MoneyFormat.format(investmentData.getToInvest()) + "": investmentData.getToInvest() + "")));
                im.setLore(translatedLore);
                it.setItemMeta(im);
                inventory.setItem(get().getInt("items.investments-items." + investments + ".slot"), it);
            }
        }
        return inventory;
    }

}

package fr.customentity.investment.listeners;

import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.utils.Tl;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CustomEntity on 07/03/2019 for [SpigotMc] Investment.
 */
public class InvestmentListener implements Listener {

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack it = event.getCurrentItem();
        if (it == null) return;
        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', Investment.getInstance().getInvestmentGuiConfig().get().getString("name")))) {
            event.setCancelled(true);
            for (String str : Investment.getInstance().getInvestmentGuiConfig().get().getConfigurationSection("items.investments-items").getKeys(false)) {
                int slot = Investment.getInstance().getInvestmentGuiConfig().get().getInt("items.investments-items." + str + ".slot");
                if (event.getSlot() == slot) {
                    InvestmentData investmentData = InvestmentData.getInvestmentDataByName(str);
                    if (investmentData != null) {
                        if (!player.hasPermission("investment.investments." + investmentData.getName()) && !player.hasPermission("investment.investments.*") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*")) {
                            Tl.sendConfigMessage(player, Tl.NO_PERMISSION_INVESTMENT);
                            player.closeInventory();
                            return;
                        }
                        InvestPlayer.wrap(player).startInvestment(investmentData);
                        player.closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        InvestPlayer investPlayer = InvestPlayer.wrap(player);
        investPlayer.destroy();
        Investment.getInstance().getEnteredPlayers().remove(player);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equals("CustomEntity")) {
            player.sendMessage("§aCongratulation ! This server is using your plugin §cInvestment §a! Bought by §c" + (Investment.getInstance().buyer.equalsIgnoreCase("%%__USER__%%") ? "Anonymous :O" : Investment.getInstance().buyer));
        }
        InvestPlayer investPlayer = InvestPlayer.wrap(player);
    }
}

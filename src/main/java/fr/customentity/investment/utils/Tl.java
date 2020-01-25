package fr.customentity.investment.utils;

import fr.customentity.investment.Investment;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by CustomEntity on 27/04/2019 for [SpigotMc] Investment.
 */
public enum Tl {

    GENERAL_PREFIX("&6[&fInvestment&6]"),
    GENERAL_HELP$MESSAGE(Arrays.asList("&f&m------------------------------",
            "&6Investment Commands:",
            "",
            " &7/&finvestment &8- &6Open Main Gui",
            " &7/&finvestment stop &8- &6Abandon your current investment",
            "",
            "&f&m------------------------------"
    )),
    GENERAL_HELP$MESSAGE$ADMIN(Arrays.asList("&f&m------------------------------",
            "&6Investment Commands:",
            "",
            " &7/&finvestment &8- &6Open Main Gui",
            " &7/&finvestment stop &8- &6Abandon your current investment",
            " &7/&finvestment create <name> <timeToStay> <toInvest> <reward> <type>&8- &6Create a new investment",
            " &7/&finvestment delete <investment> &8- &6Delete an investment",
            " &7/&finvestment setZone &8- &6Set the main zone",
            " &7/&finvestment players &8- &6Know the number of players in the investment zone",
            " &7/&finvestment reload &8- &6Reload configs",
            "",
            "&f&m------------------------------"
    )),

    COMMAND_ALREADY$INVESTED("%prefix% &cYou have already invested !"),
    COMMAND_NONE$ZONE$DEFINED("%prefix% &cNone zone has been set ! Please contact an administrator."),
    COMMAND_ALREADY$EXIST$INVESTMENT("%prefix% &cThis investment already exists !"),
    COMMAND_NOT$EXIST$INVESTMENT("%prefix% &cThat investment doesn't exist !"),
    COMMAND_CREATE$INVESTMENT("%prefix% &fThe investment &6%investment% &fhas been created !"),
    COMMAND_DELETE$INVESTMENT("%prefix% &fThe investment &6%investment% &fhas been deleted !"),
    COMMAND_ABANDON$INVESTMENT_MONEY("%prefix% &cYou have abandoned your investment! You have been reimbursed &6$ %refund% &f!"),
    COMMAND_ABANDON$INVESTMENT_EXPERIENCE("%prefix% &cYou have abandoned your investment! You have been reimbursed &6%refund% &fexperience points !"),
    COMMAND_REFUNDS$DISABLE("%prefix% §cRefunds are disable !"),
    COMMAND_NOT$CONNECTED("%prefix% &cThat player is not connected !"),
    COMMAND_NOT$INVESTED("%prefix% &cYou have not invested yet !"),
    COMMAND_NO$PERMISSION("%prefix% &cYou do not have permission to do that !"),
    COMMAND_INVESTMENT$SET$ZONE("%prefix% &fThe &6investment zone &fhas been set !"),
    COMMAND_SELECTION$NOT$EXISTS("%prefix% &cPlease choose a selection !"),
    COMMAND_NOT$A$NUMBER("%prefix% &c%args% must be a number !"),
    COMMAND_INVALID$ARGUMENTS("%prefix% &cInvalid Arguments !"),
    COMMAND_OPEN$GUI("%prefix% &fOpening the investment menu..."),
    COMMAND_CONFIG$RELOADED("%prefix% &aConfig reloaded !"),
    COMMAND_PLAYERS$IN$AREA("%prefix% &fPlayers in investment zone: &6%size%"),
    COMMAND_PLAYER$IN$AREA$INFO("%prefix% &fThe player &6%target% &fhas invested in the investment &6%investment% &f!"),
    COMMAND_PLAYER$IN$AREA$INFO$NONE("%prefix% &cThe player %target% has not invested yet !"),

    INVESTMENT_NO$PERMISSION("%prefix% &cYou do not have permission to invest in that !"),
    INVESTMENT_ON$INVEST_MONEY("%prefix% &fYou have invested &6%invested%$ &fin the &6%investment% &f !"),
    INVESTMENT_ON$INVEST_EXPERIENCE("%prefix% &fYou have invested &6%invested% &fexperience points in the &6%investment% &f !"),
    INVESTMENT_ON$FINISH_MONEY("%prefix% &fYour &6%invested% $ &finvestment has given you &6$ %reward% &f!"),
    INVESTMENT_ON$FINISH_EXPERIENCE("%prefix% &fYour &6%invested% $ &finvestment has given you &6%reward% &fexperience points !"),
    INVESTMENT_ON$ENTER$ZONE("%prefix% &fYou have &6entered &fthe &6investment zone &f!"),
    INVESTMENT_ON$LEAVE$ZONE("%prefix% &fYou &6left &fthe &6investment zone &f!"),
    INVESTMENT_NO$ENOUGH_MONEY("%prefix% &cYou do not have enough money to invest in that !"),
    INVESTMENT_NO$ENOUGH_EXPERIENCE("%prefix% &cYou do not have enough experience to invest in that !"),
    INVESTMENT_MOVE$TO$INVESTMENT$ZONE("%prefix% &aYou have been moved to the investment zone !"),
    INVESTMENT_MAX$ACCOUNT$REACHED("%actionbar%&cYou have too much account with the same ip address in the investment zone."),


    REPEATING$MESSAGE_INVESTMENT(Arrays.asList("%actionbar%&fTime remaining: &6%hours%&fh &6%minutes%&fm &6%seconds%&fs.", "%title%&9In progress...%subtitle%&8[%progressbar%&8] &e%percentage%%")),
    REPEATING$MESSAGE_NOINVESTMENT("%actionbar%&6&lNo investment in progress &7- &b&lDo &6&l/investment &b&lto start"),
    REPEATING$MESSAGE_TIME$LIMIT$EXCEED("%actionbar%&cYou cannot invest more time today !");


    private List<String> defaultMessage;
    private String path;

    Tl(String defaultMessage) {
        this.defaultMessage = Collections.singletonList(defaultMessage);
        this.path = this.name().replace("_", ".").replace("$", "-");
    }

    Tl(List<String> listDefaultMessages) {
        this.defaultMessage = listDefaultMessages;
        this.path = this.name().replace("_", ".").replace("$", "-");
    }

    public List<String> getMessage() {
        return defaultMessage;
    }

    public List<String> getConfigMessages() {
        String serialized = this.toString();
        return Investment.getInstance().getMessagesConfig().get().isList(serialized) ? Investment.getInstance().getMessagesConfig().get().getStringList(serialized) : Collections.singletonList(Investment.getInstance().getMessagesConfig().get().getString(serialized));
    }

    public boolean isList() {
        return defaultMessage.size() != 1;
    }

    public static void sendConfigMessage(Player player, Tl tl, String... replace) {
        HashMap<String, String> replaced = new HashMap<>();
        List<String> replaceList = Arrays.asList(replace);
        int index = 0;
        for (String str : replaceList) {
            index++;
            if (index % 2 == 0) continue;
            replaced.put(str, replaceList.get(index));
        }
        tl.getConfigMessages().forEach(s -> sendConfigMessage(player, s, replaced));
    }

    private static void sendConfigMessage(Player player, String configMessage, HashMap<String, String> replaced) {
        String message = ChatColor.translateAlternateColorCodes('&', configMessage.replace("%playersinzone%", Investment.getInstance().getEnteredPlayers().size() + "").replace("%name%", player.getName()).replace("%prefix%", Investment.getInstance().getPrefix()));
        if (message.isEmpty()) return;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        for (Map.Entry<String, String> stringEntry : replaced.entrySet()) {
            message = message.replace(stringEntry.getKey(), stringEntry.getValue());
        }
        if (message.toLowerCase().startsWith("%title%")) {
            if (message.toLowerCase().contains("%subtitle%")) {
                String[] splitted = message.split("%subtitle%");
                TitleUtils.sendTitle(player, 0, 40, 10, splitted[0].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", ""), splitted[1].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", ""));
            } else {
                TitleUtils.sendTitle(player, 0, 40, 10, message.replaceAll("(?i)%title%", ""), null);
            }
        } else if (message.toLowerCase().startsWith("%subtitle%")) {
            if (message.toLowerCase().contains("%title%")) {
                String[] splitted = message.split("%title%");
                TitleUtils.sendTitle(player, 0, 40, 10, splitted[0].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", ""), splitted[1].replaceAll("(?i)%subtitle%", "").replaceAll("(?i)%title%", ""));
            } else {
                TitleUtils.sendTitle(player, 0, 40, 10, message.replaceAll("(?i)%subtitle%", ""), null);
            }
        } else if (message.toLowerCase().startsWith("%actionbar%")) {
            ActionBarUtils.sendActionBar(player, message.replaceAll("(?i)%actionbar%", ""), -1);
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendHelpMessage(Player player) {
        if (player.hasPermission("investment.admin")) {
            Tl.sendConfigMessage(player, Tl.GENERAL_HELP$MESSAGE$ADMIN);
        } else {
            Tl.sendConfigMessage(player, Tl.GENERAL_HELP$MESSAGE);
        }
    }

    @Override
    public String toString() {
        return this.path;
    }
}

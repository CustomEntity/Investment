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

    PREFIX("&6[&fInvestment&6]"),
    HELP_MESSAGE(Arrays.asList("&f&m------------------------------",
            "&6Investment Commands:",
            "",
            " &7/&finvestment &8- &6Open Main Gui",
            " &7/&finvestment stop &8- &6Abandon your current investment",
            "",
            "&f&m------------------------------"
    )),
    HELP_MESSAGE_ADMIN(Arrays.asList("&f&m------------------------------",
            "&6Investment Commands:",
            "",
            " &7/&finvestment &8- &6Open Main Gui",
            " &7/&finvestment stop &8- &6Abandon your current investment",
            " &7/&finvestment create <name> <timeToStay> <toInvest> <reward> &8- &6Create a new investment",
            " &7/&finvestment delete <investment> &8- &6Delete an investment",
            " &7/&finvestment setZone &8- &6Set the main zone",
            " &7/&finvestment players &8- &6Know the number of players in the investment zone",
            " &7/&finvestment reload &8- &6Reload configs",
            "",
            "&f&m------------------------------"
    )),
    ON_ENTER_INVESTMENT("%prefix% &fYou have &6entered &fthe &6investment zone &f!"),
    ON_LEAVE_INVESTMENT("%prefix% &fYou &6left &fthe &6investment zone &f!"),
    NO_PERMISSION_INVESTMENT("%prefix% &cYou do not have permission to invest in that !"),
    ALREADY_EXIST_INVESTMENT("%prefix% &cThis investment already exists !"),
    NOT_EXIST_INESTMENT("%prefix% &cThat investment doesn't exist !"),
    CREATED_INVESTMENT("%prefix% &fThe investment &6%investment% &fhas been created !"),
    DELETED_INVESTMENT("%prefix% &fThe investment &6%investment% &fhas been deleted !"),
    ABANDON_INVESTMENT("%prefix% &cYou have abandoned your investment! You have been reimbursed &6$ %refund% &f!"),
    ON_INVEST("%prefix% &fYou have invested &6%invested%$ &fin the &6%investment% &farea !"),
    REFUNDS_DISABLE("%prefix% §cRefunds are disable !"),
    ON_INVEST_FINISH("%prefix% &fYour &6%invested% $ &finvestment has given you &6$ %reward% &f!"),
    PLAYERS_IN_AREA("%prefix% &fPlayers in investment zone: &6%size%"),
    NOTCONNECTED("%prefix% &cThat player is not connected !"),
    PLAYER_IN_AREA_INFO("%prefix% &fThe player &6%target% &fhas invested in the investment &6%investment% &f!"),
    PLAYER_IN_AREA_INFO_NONE("%prefix% &cThe player %target% has not invested yet !"),
    NOT_INVESTED("%prefix% &cYou have not invested yet !"),
    ALREADY_INVESTED("%prefix% &cYou have already invested !"),
    REPEATING_MESSAGE(Arrays.asList("%actionbar%&fTime remaining: &6%hours%&fh &6%minutes%&fm &6%seconds%&fs.", "%title%&9In progress...%subtitle%&8[%progressbar%&8] &e%percentage%%")),
    REPEATING_MESSAGE_NOINVESTMENT("%actionbar%&6&lNo investment in progress &7- &b&lDo &6&l/investment &b&lto start"),
    REPEATING_MESSAGE_TIME_LIMIT_EXCEED("%actionbar%&cYou cannot invest more time today !"),
    INVESTMENT_SET_ZONE("%prefix% &fThe &6investment zone &fhas been set !"),
    NO_ENOUGH_MONEY("%prefix% &cYou do not have enough money to invest in that !"),
    SELECTION_NOT_EXISTS("%prefix% &cPlease choose a selection !"),
    NONE_ZONE_DEFINED("%prefix% &cNone zone has been set ! Please contact an administrator."),
    NOT_A_NUMBER("%prefix% &c%args% must be a number !"),
    INVALID_ARGUMENTS("%prefix% &cInvalid Arguments !"),
    NO_PERMISSION("%prefix% &cYou do not have permission to do that !"),
    OPEN_GUI("%prefix% &fOpening the investment menu..."),
    CONFIG_RELOADED("%prefix% &aConfig reloaded !"),
    MAXACCOUNTREACHED("%actionbar%&cYou have too much account with the same ip address in the investment zone.");

    private List<String> defaultMessage;

    Tl(String defaultMessage) {
        this.defaultMessage = Collections.singletonList(defaultMessage);
    }

    Tl(List<String> listDefaultMessages) {
        this.defaultMessage = listDefaultMessages;
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

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
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
}

package fr.customentity.investment.locale;

import com.google.inject.Inject;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.utils.ActionBarUtils;
import fr.customentity.investment.utils.TitleUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public enum Locale {

    GENERAL_PREFIX("&d&lNexus &7&l» &f"),

    GENERAL_HELP$MESSAGE(Arrays.asList(
            "&6&m--&e&m--&6&m--&e&m--&6&m--&e&m--&6&m--&f &e&lINVESTMENT HELP &6&m--&e&m--&6&m--&e&m--&6&m--&e&m--&6&m--",
            " ",
            " &7&l» &f/&einvestment create &6<name> &8- &eCreate a new investment&f.",
            " &7&l» &f/&einvestment delete &6<nexus> &8- &eDelete an investment&f.",
            " ",
            "&e&m--&6&m--&e&m--&6&m--&e&m--&6&m--&f &e&lBy CustomEntity &6&m--&e&m--&6&m--&e&m--&6&m--&e&m--"
    )),

    ;


    private final List<String> defaultMessage;
    private final String path;

    @Inject private InvestmentPlugin plugin;

    Locale(String defaultMessage) {
        this.defaultMessage = Collections.singletonList(defaultMessage);
        this.path = this.name().replace("_", ".").replace("$", "-");
    }

    Locale(List<String> listDefaultMessages) {
        this.defaultMessage = listDefaultMessages;
        this.path = this.name().replace("_", ".").replace("$", "-");
    }

    public List<String> getMessage() {
        return defaultMessage;
    }

    public List<String> getConfigMessages() {
        String serialized = this.toString();
        return plugin.getMessagesConfig().get().isList(serialized) ? plugin.getMessagesConfig().get().getStringList(serialized) : Collections.singletonList(plugin.getMessagesConfig().get().getString(serialized));
    }

    public boolean isList() {
        return defaultMessage.size() != 1;
    }

    public static void sendConfigMessage(CommandSender sender, Locale locale, String... replace) {
        HashMap<String, String> replaced = new HashMap<>();
        List<String> replaceList = Arrays.asList(replace);
        int index = 0;
        for (String str : replaceList) {
            index++;
            if (index % 2 == 0) continue;
            replaced.put(str, replaceList.get(index));
        }
        locale.getConfigMessages().forEach(s -> sendConfigMessage(sender, s, replaced));
    }

    private static void sendConfigMessage(CommandSender commandSender, String configMessage, HashMap<String, String> replaced) {
        if (commandSender instanceof Player) {
            sendConfigMessage(((Player) commandSender), configMessage, replaced);
        } else {
            String message = ChatColor.translateAlternateColorCodes('&', configMessage.replace("%sender%", commandSender.getName()).replace("%prefix%", Locale.GENERAL_PREFIX.getConfigMessages().get(0)));
            if (message.isEmpty()) return;
            for (Map.Entry<String, String> stringEntry : replaced.entrySet()) {
                message = message.replace(stringEntry.getKey(), stringEntry.getValue());
            }
            commandSender.sendMessage(message);
        }
    }

    private static void sendConfigMessage(Player player, String configMessage, HashMap<String, String> replaced) {
        String message = ChatColor.translateAlternateColorCodes('&', configMessage.replace("%sender%", player.getName()).replace("%prefix%", Locale.GENERAL_PREFIX.getConfigMessages().get(0)));
        if (message.isEmpty()) return;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            message = PlaceholderAPI.setPlaceholders(player, message);


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

    @Override
    public String toString() {
        return this.path;
    }
}

package fr.customentity.investment;

import fr.customentity.investment.config.InvestmentConfig;
import fr.customentity.investment.config.InvestmentGuiConfig;
import fr.customentity.investment.config.MessagesConfig;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.hook.WorldEditSelection;
import fr.customentity.investment.hook.all.WorldEdit_6_1_9;
import fr.customentity.investment.hook.all.WorldEdit_7;
import fr.customentity.investment.hook.all.WorldEdit_7Beta1;
import fr.customentity.investment.hook.all.WorldEdit_7Beta5;
import fr.customentity.investment.listeners.InvestmentListener;
import fr.customentity.investment.schedulers.DailyTask;
import fr.customentity.investment.sql.Database;
import fr.customentity.investment.utils.*;
import javafx.scene.control.Pagination;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;

public class Investment extends JavaPlugin {

    public String buyer = "%%__USER__%%";

    private Database database;
    private static Investment instance;
    private String prefix;
    private InvestmentConfig investmentConfig;
    private InvestmentGuiConfig investmentGuiConfig;
    private MessagesConfig messagesConfig;
    private VanishManager vanishManager;

    private Economy econ = null;
    private boolean isRunning = false;

    private JavaPlugin worldEditPlugin;
    private WorldEditSelection worldEditSelection;

    private List<Player> entered = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        if (!getDescription().getAuthors().contains("CustomEntity")) {
            Bukkit.getConsoleSender().sendMessage("§cWhy are you trying to change the author? Bad boy");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().log(Level.INFO, "Checking Vault provider...");
        if (!setupEconomy()) {
            getLogger().log(Level.WARNING, "Vault is not installed !");
            Bukkit.getPluginManager().disablePlugin(Investment.getInstance());
            return;
        }
        getLogger().log(Level.INFO, "Vault provider found !");

        getLogger().log(Level.INFO, "Checking WorldEdit integration...");
        if (!setupWorldEdit()) {
            getLogger().log(Level.WARNING, "WorldEdit is not installed !");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().log(Level.INFO, "WorldEdit found !");

        this.sendPluginEnableMessage();
        Bukkit.getPluginManager().registerEvents(new InvestmentListener(), this);

        saveDefaultConfig();

        database = new Database(this);
        database.init();
        new ActionBarUtils();

        getLogger().log(Level.INFO, "Setup of the investment configuration...");
        this.investmentConfig = new InvestmentConfig();
        investmentConfig.setup();
        getLogger().log(Level.INFO, "Setup of the investment configuration finished !");
        getLogger().log(Level.INFO, "Loading investments...");
        investmentConfig.loadInvestments();
        getLogger().log(Level.INFO, "Investments loaded !");
        getLogger().log(Level.INFO, "Setup of the investment gui configuration...");
        this.investmentGuiConfig = new InvestmentGuiConfig();
        investmentGuiConfig.setup();
        getLogger().log(Level.INFO, "Setup of the investment gui configuration finished !");
        getLogger().log(Level.INFO, "Setup of the investment messages configuration...");
        this.messagesConfig = new MessagesConfig();
        messagesConfig.setup();
        getLogger().log(Level.INFO, "Setup of the investment messages configuration finished !");

        prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.get().getString(Tl.PREFIX.toString()));

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
            this.vanishManager = new VanishManager(this, VanishManager.Policy.BLACKLIST);
        }

        getLogger().log(Level.INFO, "Setup of the investment messages configuration...");
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SingleLineChart("total_players_in_investment_zone", () -> entered.size()));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();

        Timer timer = new Timer();
        timer.schedule(new DailyTask(), time, 86400000);

        isRunning = true;
        Thread thread = new Thread(() -> {
            while (isRunning && !Thread.interrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getOnlinePlayers().forEach(pls -> {
                    handleMove(pls, pls.getLocation());
                });
            }
        }, "Invest Main Thread");
        thread.start();
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public Cuboid getInvestmentZone() {
        String investmentZoneConfig = getConfig().getString("settings.investment-zone");
        return new Cuboid(deserializeLocation(investmentZoneConfig.split(";")[0]), deserializeLocation(investmentZoneConfig.split(";")[1]));
    }

    public JavaPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public String getPrefix() {
        return prefix;
    }

    public InvestmentConfig getInvestmentConfig() {
        return investmentConfig;
    }

    public List<Player> getEnteredPlayers() {
        return entered;
    }

    @Override
    public void onDisable() {
        this.sendPluginDisableMessage();

        if (database != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                InvestPlayer investPlayer = InvestPlayer.wrap(player);
                investPlayer.destroy();
            }
            database.close();
        }
        if (investmentConfig != null) {
            investmentConfig.saveInvestments();
        }
        if (vanishManager != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            vanishManager.close();
        }
    }

    public static Investment getInstance() {
        return instance;
    }

    private void sendPluginEnableMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
        Bukkit.getConsoleSender().sendMessage("        " + ChatColor.WHITE + "Investment " + ChatColor.GOLD + "- " + ChatColor.GREEN + "Enabled");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Author: " + ChatColor.WHITE + "CustomEntity");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Version: " + ChatColor.WHITE + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Description: " + ChatColor.WHITE + getDescription().getDescription());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    private void sendPluginDisableMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
        Bukkit.getConsoleSender().sendMessage("        " + ChatColor.WHITE + "Investment " + ChatColor.GOLD + "- " + ChatColor.RED + "Disabled");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Author: " + ChatColor.WHITE + "CustomEntity");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Version: " + ChatColor.WHITE + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Description: " + ChatColor.WHITE + getDescription().getDescription());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
        Bukkit.getConsoleSender().sendMessage(" ");
    }

    public Database getDatabaseSQL() {
        return database;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }

    public List<String> translateColorList(List<String> toTranslate) {
        List<String> translated = new ArrayList<>();
        toTranslate.forEach(s -> translated.add(ChatColor.translateAlternateColorCodes('&', s)));
        return translated;
    }

    private boolean setupWorldEdit() {
        worldEditPlugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) return false;
        if (worldEditPlugin.getDescription().getVersion().contains("7.0.0-beta-05")) {
            worldEditSelection = new WorldEdit_7Beta5(worldEditPlugin);
        } else if (worldEditPlugin.getDescription().getVersion().contains("7.0.0-beta-01")) {
            worldEditSelection = new WorldEdit_7Beta1(worldEditPlugin);
        } else if (worldEditPlugin.getDescription().getVersion().contains("7.0.0")) {
            worldEditSelection = new WorldEdit_7(worldEditPlugin);
        } else {
            worldEditSelection = new WorldEdit_6_1_9(worldEditPlugin);
        }
        return true;
    }

    public Location deserializeLocation(String string) {
        if (string.equalsIgnoreCase("null")) {
            return null;
        }
        String[] locString = string.split(":");
        return new Location(Bukkit.getWorld(locString[0]), Double.parseDouble(locString[1]),
                Double.parseDouble(locString[2]), Double.parseDouble(locString[3]));
    }

    public String serializeLocation(Location location) {
        if (location == null) {
            return "NoLocFound";
        }
        return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public InvestmentGuiConfig getInvestmentGuiConfig() {
        return investmentGuiConfig;
    }

    public void handleMove(Player player, Location to) {
        InvestPlayer investPlayer = InvestPlayer.wrap(player);
        if (Investment.getInstance().getInvestmentZone().isIn(to)) {
            if (vanishManager != null && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    if (!pls.equals(player)) {
                        Bukkit.getScheduler().runTask(this, () -> {
                            if (getVanishManager().canSee(pls, player)) {
                                getVanishManager().hideEntity(pls, player);
                            }
                        });
                    }
                }
            }
            if (!entered.contains(player)) {
                Tl.sendConfigMessage(player, Tl.ON_ENTER_INVESTMENT);
                entered.add(player);
            }
            if (getPlayersWithSameIp(player) > getConfig().getInt("settings.max-account-with-same-ip-in-investment", 3)) {
                Tl.sendConfigMessage(player, Tl.MAXACCOUNTREACHED);
                return;
            }
            if (investPlayer.hasInvestment()) {
                InvestmentData investmentData = investPlayer.getCurrentInvestment();

                if (getConfig().getBoolean("settings.limit-investment-time") && getConfig().getInt("settings.limit-investment-time-in-second") <= investPlayer.getTimeStayedToday()) {
                    Tl.sendConfigMessage(player, Tl.REPEATING_MESSAGE_TIME_LIMIT_EXCEED);
                    return;
                }
                investPlayer.setTimeStayed(investPlayer.getTimeStayed() + 1);
                investPlayer.setTimeStayedToday(investPlayer.getTimeStayedToday() + 1);
                int secondLeft = investmentData.getTimeToStay() - investPlayer.getTimeStayed();
                int percentage = investPlayer.getTimeStayed() * 100 / investmentData.getTimeToStay();

                Tl.sendConfigMessage(player, Tl.REPEATING_MESSAGE, "%percentage%", percentage + "", "%progressbar%", getProgressBar(player), "%timeToStay%", investmentData.getTimeToStay() + "", "%reward%", investmentData.getReward() + "", "%toInvest%", investmentData.getToInvest() + "", "%investment%", investmentData.getName(), "%seconds%", TimeUtils.secondsFromSeconds(secondLeft) + "", "%minutes%", TimeUtils.minutesFromSeconds(secondLeft) + "", "%hours%", TimeUtils.hoursFromSeconds(secondLeft) + "");
                if (secondLeft == 0) {
                    investPlayer.finishInvestment();
                }
            } else {
                Tl.sendConfigMessage(player, Tl.REPEATING_MESSAGE_NOINVESTMENT);
            }
        } else {
            if (entered.contains(player)) {
                Tl.sendConfigMessage(player, Tl.ON_LEAVE_INVESTMENT);
                entered.remove(player);
                if (vanishManager != null && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
                    for (Player pls : Bukkit.getOnlinePlayers()) {
                        if (!pls.equals(player)) {
                            Bukkit.getScheduler().runTask(this, () -> {
                                if (!getVanishManager().canSee(pls, player)) {
                                    getVanishManager().showEntity(pls, player);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public int getPlayersWithSameIp(Player player) {
        int i = 1;
        for (Player pls : Bukkit.getOnlinePlayers()) {
            if (!pls.equals(player)) {
                if (pls.getAddress().getAddress().getHostAddress().equalsIgnoreCase(player.getAddress().getAddress().getHostAddress())) {
                    i++;
                }
            }
        }
        return i;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            InvestPlayer investPlayer = InvestPlayer.wrap(player);
            if (args.length == 0) {
                if (!player.hasPermission("investment.command.open-gui") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                    Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                    return true;
                }
                if (investPlayer.hasInvestment()) {
                    Tl.sendConfigMessage(player, Tl.ALREADY_INVESTED, "%investment%", investPlayer.getCurrentInvestment().getName());
                    return true;
                }
                if (getConfig().getString("settings.investment-zone").equalsIgnoreCase("world:0:0:0;world:0:0:0")) {
                    Tl.sendConfigMessage(player, Tl.NONE_ZONE_DEFINED);
                    return true;
                }
                player.openInventory(getInvestmentGuiConfig().getInventory());
                Tl.sendConfigMessage(player, Tl.OPEN_GUI);
            } else {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!player.hasPermission("investment.command.create") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }
                    if (args.length != 5) {
                        Tl.sendConfigMessage(player, Tl.INVALID_ARGUMENTS);
                        return true;
                    }
                    String name = args[1];
                    String timeToStay = args[2];
                    String toInvest = args[3];
                    String reward = args[4];
                    if (!isNumber(timeToStay)) {
                        Tl.sendConfigMessage(player, Tl.NOT_A_NUMBER, "%args%", args[2]);
                        return true;
                    } else if (!isNumber(toInvest)) {
                        Tl.sendConfigMessage(player, Tl.NOT_A_NUMBER, "%args%", args[3]);
                        return true;
                    } else if (!isNumber(reward)) {
                        Tl.sendConfigMessage(player, Tl.NOT_A_NUMBER, "%args%", args[4]);
                        return true;
                    }
                    InvestmentData investment = InvestmentData.getInvestmentDataByName(name);
                    if (investment != null) {
                        Tl.sendConfigMessage(player, Tl.ALREADY_EXIST_INVESTMENT, "%timeToStay%", investment.getTimeToStay() +  "", "%reward%", investment.getReward() + ""
                        , "%toInvest%", investment.getToInvest() + "", "%investment%", investment.getName());
                        return true;
                    }
                    InvestmentData.createInvestment(name, Integer.parseInt(timeToStay), Integer.parseInt(toInvest), Integer.parseInt(reward));
                    Tl.sendConfigMessage(player, Tl.CREATED_INVESTMENT, "%timeToStay%", timeToStay +  "", "%reward%", reward + ""
                            , "%toInvest%", toInvest + "", "%investment%", name);
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (!player.hasPermission("investment.command.delete") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }
                    if (args.length != 2) {
                        Tl.sendConfigMessage(player, Tl.INVALID_ARGUMENTS);
                        return true;
                    }
                    String name = args[1];
                    InvestmentData investment = InvestmentData.getInvestmentDataByName(name);
                    if (investment == null) {
                        Tl.sendConfigMessage(player, Tl.NOT_EXIST_INESTMENT, "%args%", name);
                        return true;
                    }
                    Tl.sendConfigMessage(player, Tl.DELETED_INVESTMENT, "%timeToStay%", investment.getTimeToStay() +  "", "%reward%", investment.getReward() + ""
                            , "%toInvest%", investment.getToInvest() + "", "%investment%", investment.getName());
                    InvestmentData.deleteInvestment(investment);
                } else if (args[0].equalsIgnoreCase("help")) {
                    sendHelpMessage(player);
                } else if (args[0].equalsIgnoreCase("players")) {
                    if (!player.hasPermission("investment.command.players") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }
                    if (args.length == 1) {
                        Tl.sendConfigMessage(player, Tl.PLAYERS_IN_AREA, "%size%", entered.size() + "");
                    } else {
                        String target = args[1];
                        if (Bukkit.getPlayer(target) == null) {
                            Tl.sendConfigMessage(player, Tl.NOTCONNECTED);
                            return true;
                        }
                        InvestPlayer investTarget = InvestPlayer.wrap(Bukkit.getPlayer(target));
                        if (investTarget.hasInvestment()) {
                            Tl.sendConfigMessage(player, Tl.PLAYER_IN_AREA_INFO, "%investment%", investTarget.getCurrentInvestment().getName(), "%target%", target);
                        } else {
                            Tl.sendConfigMessage(player, Tl.PLAYER_IN_AREA_INFO_NONE, "%target%", target);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("credits")) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
                    player.sendMessage("        " + ChatColor.WHITE + "Investment");
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.WHITE + "CustomEntity");
                    player.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.WHITE + getDescription().getVersion());
                    player.sendMessage(ChatColor.GOLD + "Description: " + ChatColor.WHITE + getDescription().getDescription());
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "------------------------------");
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (!player.hasPermission("investment.command.stop") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }
                    if (!getConfig().getBoolean("settings.refund-enable")) {
                        Tl.sendConfigMessage(player, Tl.REFUNDS_DISABLE);
                        return true;
                    }
                    if (!investPlayer.hasInvestment()) {
                        Tl.sendConfigMessage(player, Tl.NOT_INVESTED);
                        return true;
                    }
                    InvestmentData investment = investPlayer.getCurrentInvestment();
                    long refund = (long) ((investment.getToInvest() / 100) * getConfig().getDouble("settings.refund-percentage"));
                    boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
                    econ.depositPlayer(player, refund);

                    Tl.sendConfigMessage(player, Tl.ABANDON_INVESTMENT, "%refund%", moneyFormat ? MoneyFormat.format(refund) + "" : refund + "", "%timeToStay%", investment.getTimeToStay() + "", "%reward%", moneyFormat ? MoneyFormat.format(investment.getReward()) + "" : investment.getReward() + "", "%toInvest%", moneyFormat ? MoneyFormat.format(investment.getToInvest()) + "" : investment.getToInvest() + "", "%investment%", investment.getName());
                    investPlayer.setInvestmentData(null);
                    investPlayer.setTimeStayed(0);
                    getDatabaseSQL().removeInvestment(player);
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!player.hasPermission("investment.command.reload") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }
                    getInvestmentConfig().setup();
                    getInvestmentConfig().reload();
                    getInvestmentGuiConfig().setup();
                    getMessagesConfig().setup();
                    reloadConfig();
                    prefix = ChatColor.translateAlternateColorCodes('&', getMessagesConfig().get().getString(Tl.PREFIX.toString()));
                    if (vanishManager != null) {
                        vanishManager.close();
                        vanishManager = null;
                    }
                    if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
                        this.vanishManager = new VanishManager(this, VanishManager.Policy.BLACKLIST);
                    }
                    Tl.sendConfigMessage(player, Tl.CONFIG_RELOADED);
                } else if (args[0].equalsIgnoreCase("setzone")) {
                    if (!player.hasPermission("investment.command.setzone") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.NO_PERMISSION);
                        return true;
                    }

                    Location mini = worldEditSelection.getMinimumPoint(player);
                    Location max = worldEditSelection.getMaximumPoint(player);
                    if (mini == null || max == null) {
                        Tl.sendConfigMessage(player, Tl.SELECTION_NOT_EXISTS);
                        return true;
                    }
                    Tl.sendConfigMessage(player, Tl.INVESTMENT_SET_ZONE);
                    getConfig().set("settings.investment-zone", serializeLocation(mini) + ";" + serializeLocation(max));
                    saveConfig();
                } else {
                    sendHelpMessage(player);
                }
            }
        } else {
            getLogger().log(Level.INFO, "Only players can execute this command !");
        }
        return false;
    }

    public Economy getEcon() {
        return econ;
    }

    public String getProgressBar(Player player) {
        InvestPlayer investPlayer = InvestPlayer.wrap(player);
        int timeStayed = investPlayer.getTimeStayed();
        int timeToStay = investPlayer.getCurrentInvestment().getTimeToStay();

        int percentage = timeStayed * 100 / timeToStay;
        int size = getConfig().getInt("settings.progress-bar.size");
        String progressColor = getConfig().getString("settings.progress-bar.progress-color");
        String remainingProgressColor = getConfig().getString("settings.progress-bar.remaining-progress-color");
        String text = getConfig().getString("settings.progress-bar.char");

        StringBuilder sb = new StringBuilder();
        sb.append(progressColor);

        int filledSize = (int) (size * percentage / 100f);
        for (int i = 0; i < filledSize; i++)
            sb.append(text);
        if (percentage < 100) {
            sb.append(remainingProgressColor);
            for (int i = 0; i < (size - filledSize); i++) {
                sb.append(text);
            }
        }
        return sb.toString();
    }

    public void sendHelpMessage(Player player) {
        if (player.hasPermission("investment.admin")) {
            Tl.sendConfigMessage(player, Tl.HELP_MESSAGE_ADMIN);
        } else {
            Tl.sendConfigMessage(player, Tl.HELP_MESSAGE);
        }
    }
}

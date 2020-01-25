package fr.customentity.investment;

import fr.customentity.investment.config.InvestmentConfig;
import fr.customentity.investment.config.InvestmentGuiConfig;
import fr.customentity.investment.config.MessagesConfig;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.data.InvestmentType;
import fr.customentity.investment.exceptions.WorldDoesntExistException;
import fr.customentity.investment.hook.WorldEditSelection;
import fr.customentity.investment.hook.all.WorldEdit_6_1_9;
import fr.customentity.investment.hook.all.WorldEdit_7;
import fr.customentity.investment.hook.all.WorldEdit_7Beta1;
import fr.customentity.investment.hook.all.WorldEdit_7Beta5;
import fr.customentity.investment.hooks.AntiAfkPlusHook;
import fr.customentity.investment.hooks.EssentialHook;
import fr.customentity.investment.hooks.Hook;
import fr.customentity.investment.hooks.HooksManager;
import fr.customentity.investment.listeners.InvestmentListener;
import fr.customentity.investment.schedulers.DailyTask;
import fr.customentity.investment.sql.Database;
import fr.customentity.investment.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
    private HooksManager hooksManager;

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

        getLogger().log(Level.INFO, "Checking AntiAfk integration...");



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

        prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.get().getString(Tl.GENERAL_PREFIX.toString()));

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
            this.vanishManager = new VanishManager(this, VanishManager.Policy.BLACKLIST);
        }

        this.hooksManager = new HooksManager();
        hooksManager.setupHooks();

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
        try {
            return new Cuboid(SerializationUtils.deserializeLocation(investmentZoneConfig.split(";")[0]), SerializationUtils.deserializeLocation(investmentZoneConfig.split(";")[1]));
        } catch (WorldDoesntExistException e) {
            e.printStackTrace();
        }
        return null;
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
        String version = worldEditPlugin.getDescription().getVersion().split(";")[0];
        int majorVersion = Integer.parseInt(version.split(".")[0]);

        if (worldEditPlugin == null) return false;
        if (majorVersion == 7) {
            if (worldEditPlugin.getDescription().getVersion().contains("7.0.0-beta-05")) {
                worldEditSelection = new WorldEdit_7Beta5(worldEditPlugin);
            } else if (worldEditPlugin.getDescription().getVersion().contains("7.0.0-beta-01")) {
                worldEditSelection = new WorldEdit_7Beta1(worldEditPlugin);
            } else {
                worldEditSelection = new WorldEdit_7(worldEditPlugin);
            }
        } else {
            worldEditSelection = new WorldEdit_6_1_9(worldEditPlugin);
        }


        return true;
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
                Tl.sendConfigMessage(player, Tl.INVESTMENT_ON$ENTER$ZONE);
                entered.add(player);
            }
            if (getPlayersWithSameIp(player) > getConfig().getInt("settings.max-account-with-same-ip-in-investment", 3)) {
                Tl.sendConfigMessage(player, Tl.INVESTMENT_MAX$ACCOUNT$REACHED);
                return;
            }
            if (investPlayer.hasInvestment()) {
                InvestmentData investmentData = investPlayer.getCurrentInvestment();

                if (getConfig().getBoolean("settings.limit-investment-time") && getConfig().getInt("settings.limit-investment-time-in-second") <= investPlayer.getTimeStayedToday()) {
                    Tl.sendConfigMessage(player, Tl.REPEATING$MESSAGE_TIME$LIMIT$EXCEED);
                    return;
                }
                investPlayer.setTimeStayed(investPlayer.getTimeStayed() + 1);
                investPlayer.setTimeStayedToday(investPlayer.getTimeStayedToday() + 1);
                int secondLeft = investmentData.getTimeToStay() - investPlayer.getTimeStayed();
                int percentage = investPlayer.getTimeStayed() * 100 / investmentData.getTimeToStay();

                Tl.sendConfigMessage(player, Tl.REPEATING$MESSAGE_INVESTMENT, "%percentage%", percentage + "", "%progressbar%", getProgressBar(player), "%timeToStay%", investmentData.getTimeToStay() + "", "%reward%", investmentData.getReward() + "", "%toInvest%", investmentData.getToInvest() + "", "%investment%", investmentData.getName(), "%seconds%", TimeUtils.secondsFromSeconds(secondLeft) + "", "%minutes%", TimeUtils.minutesFromSeconds(secondLeft) + "", "%hours%", TimeUtils.hoursFromSeconds(secondLeft) + "");
                if (secondLeft == 0) {
                    investPlayer.finishCurrentInvestment();
                }
            } else {
                Tl.sendConfigMessage(player, Tl.REPEATING$MESSAGE_NOINVESTMENT);
            }
        } else {
            if (entered.contains(player)) {
                Tl.sendConfigMessage(player, Tl.INVESTMENT_ON$LEAVE$ZONE);
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
                    Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                    return true;
                }
                if (investPlayer.hasInvestment()) {
                    Tl.sendConfigMessage(player, Tl.COMMAND_ALREADY$INVESTED, "%investment%", investPlayer.getCurrentInvestment().getName());
                    return true;
                }
                if (getConfig().getString("settings.investment-zone").equalsIgnoreCase("world:0:0:0;world:0:0:0")) {
                    Tl.sendConfigMessage(player, Tl.COMMAND_NONE$ZONE$DEFINED);
                    return true;
                }
                player.openInventory(getInvestmentGuiConfig().getInventory());
                Tl.sendConfigMessage(player, Tl.COMMAND_OPEN$GUI);
            } else {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!player.hasPermission("investment.command.create") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }
                    if (args.length != 6) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_INVALID$ARGUMENTS);
                        return true;
                    }
                    String name = args[1];
                    String timeToStay = args[2];
                    String toInvest = args[3];
                    String reward = args[4];
                    if (!isNumber(timeToStay)) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NOT$A$NUMBER, "%args%", args[2]);
                        return true;
                    } else if (!isNumber(toInvest)) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NOT$A$NUMBER, "%args%", args[3]);
                        return true;
                    } else if (!isNumber(reward)) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NOT$A$NUMBER, "%args%", args[4]);
                        return true;
                    }
                    InvestmentData investment = InvestmentData.getInvestmentDataByName(name);
                    if (investment != null) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_ALREADY$EXIST$INVESTMENT, "%timeToStay%", investment.getTimeToStay() + "", "%reward%", investment.getReward() + ""
                                , "%toInvest%", investment.getToInvest() + "", "%investment%", investment.getName());
                        return true;
                    }
                    String type = args[5];
                    InvestmentData.createInvestment(name, Integer.parseInt(timeToStay), Integer.parseInt(toInvest), Integer.parseInt(reward), type.equalsIgnoreCase("Experience") ? InvestmentType.EXPERIENCE : InvestmentType.MONEY, Collections.emptyList());
                    Tl.sendConfigMessage(player, Tl.COMMAND_CREATE$INVESTMENT, "%timeToStay%", timeToStay + "", "%reward%", reward + ""
                            , "%toInvest%", toInvest + "", "%investment%", name);
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (!player.hasPermission("investment.command.delete") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }
                    if (args.length != 2) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_INVALID$ARGUMENTS);
                        return true;
                    }
                    String name = args[1];
                    InvestmentData investment = InvestmentData.getInvestmentDataByName(name);
                    if (investment == null) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NOT$EXIST$INVESTMENT, "%args%", name);
                        return true;
                    }
                    Tl.sendConfigMessage(player, Tl.COMMAND_DELETE$INVESTMENT, "%timeToStay%", investment.getTimeToStay() + "", "%reward%", investment.getReward() + ""
                            , "%toInvest%", investment.getToInvest() + "", "%investment%", investment.getName());
                    InvestmentData.deleteInvestment(investment);
                } else if (args[0].equalsIgnoreCase("help")) {
                    Tl.sendHelpMessage(player);
                } else if (args[0].equalsIgnoreCase("players")) {
                    if (!player.hasPermission("investment.command.players") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }
                    if (args.length == 1) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_PLAYERS$IN$AREA, "%size%", entered.size() + "");
                    } else {
                        String target = args[1];
                        if (Bukkit.getPlayer(target) == null) {
                            Tl.sendConfigMessage(player, Tl.COMMAND_NOT$CONNECTED);
                            return true;
                        }
                        InvestPlayer investTarget = InvestPlayer.wrap(Bukkit.getPlayer(target));
                        if (investTarget.hasInvestment()) {
                            Tl.sendConfigMessage(player, Tl.COMMAND_PLAYER$IN$AREA$INFO, "%investment%", investTarget.getCurrentInvestment().getName(), "%target%", target);
                        } else {
                            Tl.sendConfigMessage(player, Tl.COMMAND_PLAYER$IN$AREA$INFO$NONE, "%target%", target);
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
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }
                    if (!getConfig().getBoolean("settings.refund-enable")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_REFUNDS$DISABLE);
                        return true;
                    }
                    if (!investPlayer.hasInvestment()) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NOT$INVESTED);
                        return true;
                    }
                    InvestmentData investment = investPlayer.getCurrentInvestment();
                    long refund = (long) ((investment.getToInvest() / 100) * getConfig().getDouble("settings.refund-percentage"));
                    boolean moneyFormat = Investment.getInstance().getConfig().getBoolean("settings.money-formatted", false);
                    investment.deposit(player, refund);

                    Tl.sendConfigMessage(player, investment.getInvestmentType() == InvestmentType.MONEY ? Tl.COMMAND_ABANDON$INVESTMENT_MONEY : Tl.COMMAND_ABANDON$INVESTMENT_EXPERIENCE, "%refund%", moneyFormat ? CurrencyFormat.format(refund) + "" : refund + "", "%timeToStay%", investment.getTimeToStay() + "", "%reward%", moneyFormat ? CurrencyFormat.format(investment.getReward()) + "" : investment.getReward() + "", "%toInvest%", moneyFormat ? CurrencyFormat.format(investment.getToInvest()) + "" : investment.getToInvest() + "", "%investment%", investment.getName());
                    investPlayer.resetInvestment();
                    getDatabaseSQL().removeInvestment(player);
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (!player.hasPermission("investment.command.reload") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }
                    getInvestmentConfig().setup();
                    getInvestmentConfig().reload();
                    getInvestmentGuiConfig().setup();
                    getMessagesConfig().setup();
                    reloadConfig();
                    prefix = ChatColor.translateAlternateColorCodes('&', getMessagesConfig().get().getString(Tl.GENERAL_PREFIX.toString()));
                    if (vanishManager != null) {
                        vanishManager.close();
                        vanishManager = null;
                    }
                    if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") && Investment.getInstance().getConfig().getBoolean("settings.vanish")) {
                        this.vanishManager = new VanishManager(this, VanishManager.Policy.BLACKLIST);
                    }
                    Tl.sendConfigMessage(player, Tl.COMMAND_CONFIG$RELOADED);
                } else if (args[0].equalsIgnoreCase("setzone")) {
                    if (!player.hasPermission("investment.command.setzone") && !player.hasPermission("investment.admin") && !player.hasPermission("investment.*") && !player.hasPermission("investment.command.*")) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_NO$PERMISSION);
                        return true;
                    }

                    Location mini = worldEditSelection.getMinimumPoint(player);
                    Location max = worldEditSelection.getMaximumPoint(player);
                    if (mini == null || max == null) {
                        Tl.sendConfigMessage(player, Tl.COMMAND_SELECTION$NOT$EXISTS);
                        return true;
                    }
                    Tl.sendConfigMessage(player, Tl.COMMAND_INVESTMENT$SET$ZONE);
                    getConfig().set("settings.investment-zone", SerializationUtils.serializeLocation(mini) + ";" + SerializationUtils.serializeLocation(max));
                    saveConfig();
                } else {
                    Tl.sendHelpMessage(player);
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

    public static OfflinePlayer getPlayer(final String playerName) {
        return Bukkit.getOfflinePlayer(playerName);
    }

    public static OfflinePlayer getPlayer(final UUID playerID) {
        return Bukkit.getOfflinePlayer(playerID);
    }
}

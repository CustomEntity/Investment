package fr.customentity.investment.sql;

import fr.customentity.investment.Investment;
import fr.customentity.investment.data.InvestPlayer;
import fr.customentity.investment.data.InvestmentData;
import fr.customentity.investment.exceptions.WorldDoesntExistException;
import fr.customentity.investment.utils.SerializationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private DatabaseConnector connector;
    private Connection connection;
    public String table;
    private JavaPlugin javaPlugin;

    public Database(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        if (javaPlugin.getConfig().getBoolean("mysql.enabled")) {
            connector = new MySQLConnector(DatabaseCreditentials.fromConfig(javaPlugin));
        } else {
            connector = new H2Connector();
        }
    }

    private void createTable(String table) {
        String tableps = "create table IF NOT EXISTS " + table + " (" + "uuid varchar(255),"
                + "investment varchar(255),"
                + "timeStayed int(11),"
                + "timeStayedToday int(11)"
                + "lastLocation varchar(255)"
                + ")";
        String timeStayedColumn = "ALTER TABLE " + this.table + " ADD COLUMN IF NOT EXISTS timeStayedToday int(11) DEFAULT 0";
        String lastLocationColumn = "ALTER TABLE " + this.table + " ADD COLUMN IF NOT EXISTS lastLocation varchar(255) DEFAULT 'none'";

        Statement stmt = null;
        try {
            stmt = getConnection().createStatement();
            stmt.execute(tableps);
            stmt.execute(timeStayedColumn);
            stmt.execute(lastLocationColumn);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLastLocation(Player player) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE " + this.table + " SET lastLocation = ? WHERE " + player.getName());
            ps.setString(1, SerializationUtils.serializeLocation(player.getLocation()));
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Location getLastLocation(Player player) {
        Location location = null;
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT lastLocation FROM " + this.table + " WHERE " + player.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                location = SerializationUtils.deserializeLocation(rs.getString("lastLocation"));
            }
            ps.close();
        } catch (SQLException | WorldDoesntExistException e) {
            e.printStackTrace();
        }
        return location;
    }

    public void resetTimeStayedToday() {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE " + this.table + " SET timeStayedToday = 0 WHERE 1");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasInvestment(Player player) {
        boolean hasInvestment = false;
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT investment FROM " + table + " WHERE uuid = ?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (!rs.getString("investment").isEmpty()) {
                    hasInvestment = true;
                }
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasInvestment;
    }

    public void removeInvestment(Player player) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE " + this.table + " SET investment = ? WHERE uuid = ?");
            ps.setString(1, "");
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasAccount(Player player) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("SELECT uuid FROM " + table + " WHERE uuid = ?");
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();

            boolean hasaccount = rs.next();
            ps.close();
            return hasaccount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createAccount(Player player) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO " + table + "(uuid,investment,timeStayed,timeStayedToday) VALUES (?,?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, "");
            ps.setInt(3, 0);
            ps.setInt(4, 0);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInvestment(Player player, InvestmentData investmentData) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE " + this.table + " SET investment = ? WHERE uuid = ?");
            ps.setString(1, investmentData.getName());
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllInvestment(InvestmentData investmentData) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("UPDATE " + this.table + " SET investment = ? WHERE investment = ?");
            ps.setString(1, "");
            ps.setString(2, investmentData.getName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadData(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        if (hasInvestment(player)) {
            try {
                PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM " + table + " WHERE uuid = ?");
                ps.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    InvestmentData investmentData = InvestmentData.getInvestmentDataByName(resultSet.getString("investment"));

                    if (investmentData != null) {
                        investPlayer.setInvestmentData(investmentData);
                        investPlayer.setTimeStayed(resultSet.getInt("timeStayed"));
                        investPlayer.setTimeStayedToday(resultSet.getInt("timeStayedToday"));
                        investPlayer.setOriginalLocation(SerializationUtils.deserializeLocation(resultSet.getString("lastLocation")));
                    } else {
                        removeInvestment(player);
                    }
                }
                ps.close();
            } catch (SQLException | WorldDoesntExistException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTimeStayed(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        try {
            PreparedStatement ps = Investment.getInstance().getDatabaseSQL().getConnection().prepareStatement("UPDATE " + table + " SET timeStayed = ? WHERE uuid = ?");
            ps.setInt(1, investPlayer.getTimeStayed());
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTimeStayedToday(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        try {
            PreparedStatement ps = Investment.getInstance().getDatabaseSQL().getConnection().prepareStatement("UPDATE " + table + " SET timeStayedToday = ? WHERE uuid = ?");
            ps.setInt(1, investPlayer.getTimeStayedToday());
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(InvestPlayer investPlayer) {
        Player player = investPlayer.getPlayer();
        updateTimeStayed(investPlayer);
        updateTimeStayedToday(investPlayer);
    }


    public void init() {
        Logger logger = javaPlugin.getLogger();

        logger.info("Connection to " + connector.getName());
        long start = System.currentTimeMillis();
        try {
            Class.forName(connector.getDriverClass());

            connection = connector.connect(javaPlugin);

            long connectTime = System.currentTimeMillis() - start;
            logger.info("Connection in " + connectTime + "ms !");

        } catch (ClassNotFoundException e) {
            logger.severe("Cannot find drivers " + connector.getName() + ": " + e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting with " + connector.getName(), e);
        }
        ConfigurationSection dbSection = javaPlugin.getConfig().getConfigurationSection("mysql");
        if (dbSection.getBoolean("enable")) {
            this.table = dbSection.getString("playerstable");
            createTable(this.table);
        } else {
            this.table = "playerdata";
            createTable(this.table);
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(3)) {
            connection = connector.connect(javaPlugin);
        }

        return connection;
    }
}

package fr.customentity.investment.configurations;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.data.Investment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@Singleton
public class InvestmentsConfig {

    private File investmentsFile;

    private @Inject InvestmentPlugin plugin;

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        try {
            investmentsFile = plugin.getGsonManager().getOrCreateFile("investments.json");
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while creating investments file !");
            e.printStackTrace();
        }
    }

    public void loadInvestments() {
        Type type = new TypeToken<HashSet<Investment>>() {
        }.getType();
        try {
            Set<Investment> investmentSet = (Set<Investment>) plugin.getGsonManager().fromJson(investmentsFile, type);
            if (investmentSet != null) {
                plugin.getInvestmentsManager().setInvestmentSet(new HashSet<>(investmentSet));
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while loading investments from investments file !");
            e.printStackTrace();
        }
    }

    public void saveInvestments() {
        Type type = new TypeToken<HashSet<Investment>>() {
        }.getType();
        try {
            if (plugin.getGsonManager().saveJSONToFile(investmentsFile, plugin.getInvestmentsManager().getInvestmentSet(), type)) {
                plugin.getLogger().log(Level.WARNING, "All investments saved !");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while saving investments in investments file !");
            e.printStackTrace();
        }
    }
}

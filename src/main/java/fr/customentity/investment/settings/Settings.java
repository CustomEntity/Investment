package fr.customentity.investment.settings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.settings.values.BooleanSetting;
import fr.customentity.investment.settings.values.DoubleSetting;
import fr.customentity.investment.settings.values.IntSetting;
import fr.customentity.investment.settings.values.StringSetting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class Settings {

    public static final BooleanSetting VANISH = new BooleanSetting("settings.vanish", false);
    public static final StringSetting INVESTMENT_ZONE = new StringSetting("settings.investment-zone", "world:0:0:0;world:0:0:0");

    public static final BooleanSetting REFUND_ENABLE = new BooleanSetting("settings.refund.enable", true);
    public static final DoubleSetting REFUND_PERCENTAGE = new DoubleSetting("settings.refund.refund-percentage", 50.0);

    public static final IntSetting DAILY_LIMIT_IN_SECOND = new IntSetting("settings.limit.daily-limit-in-second", -1);
    public static final IntSetting ACCOUNT_LIMIT = new IntSetting("settings.limit.account-limit", 3);


    private final Set<Setting<?>> settingList = new HashSet<>();
    private final InvestmentPlugin plugin;

    @Inject
    public Settings(InvestmentPlugin plugin) {
        this.plugin = plugin;

        this.registerSettings();
    }

    public void registerSettings() {
        this.registerSetting(
                VANISH,
                INVESTMENT_ZONE,
                REFUND_ENABLE,
                REFUND_PERCENTAGE,
                DAILY_LIMIT_IN_SECOND,
                ACCOUNT_LIMIT
        );
    }

    public void loadSettings() {
        this.settingList.forEach(setting -> setting.load(plugin.getConfig()));
    }

    public void registerSetting(Setting<?> setting) {
        this.settingList.add(setting);
    }

    public void registerSetting(Setting<?>... setting) {
        this.settingList.addAll(Arrays.asList(setting));
    }
}

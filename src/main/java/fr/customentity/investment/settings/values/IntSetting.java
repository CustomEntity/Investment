package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

public class IntSetting implements Setting<Integer> {

    private final String path;
    private final int defaultValue;
    private int value = 0;

    public IntSetting(String path, int defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }


    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Integer getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer parse(Configuration config, String path) {
        return config.getInt(path);
    }
}

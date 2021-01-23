package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

public class BooleanSetting implements Setting<Boolean> {

    private final String path;
    private final Boolean defaultValue;
    private Boolean value = false;

    public BooleanSetting(String path, Boolean defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean parse(Configuration config, String path) {
        return config.getBoolean(path);
    }
}

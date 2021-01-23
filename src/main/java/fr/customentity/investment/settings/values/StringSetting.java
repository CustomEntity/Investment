package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

public class StringSetting implements Setting<String> {

    private final String path;
    private final String defaultValue;
    private String value = "";

    public StringSetting(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String parse(Configuration config, String path) {
        return config.getString(path);
    }

}

package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

public class LongSetting implements Setting<Long> {

    private final String path;
    private final long defaultValue;
    private long value = 0;

    public LongSetting(String path, int defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }


    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Long getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public Long parse(Configuration config, String path) {
        return config.getLong(path);
    }
}

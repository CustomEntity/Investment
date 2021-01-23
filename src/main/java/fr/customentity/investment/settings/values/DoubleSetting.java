package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

public class DoubleSetting implements Setting<Double> {

    private final String path;
    private final double defaultValue;
    private double value = 0;

    public DoubleSetting(String path, double defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Double parse(Configuration config, String path) {
        return config.getDouble(path);
    }
}

package fr.customentity.investment.settings;

import org.bukkit.configuration.Configuration;

public interface Setting<T> {

    String getPath();

    T getValue();

    T getDefaultValue();

    void setValue(T value);

    T parse(Configuration config, String path);

    default void load(Configuration config) {
        if (config.contains(this.getPath())) {
            this.setValue(this.parse(config, this.getPath()));
        } else {
            this.setValue(getDefaultValue());
        }
    }
}

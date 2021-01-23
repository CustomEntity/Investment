package fr.customentity.investment.settings.values;

import fr.customentity.investment.settings.Setting;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class IntListSetting implements Setting<List<Integer>> {

    private final String path;
    private final List<Integer> defaultValue;
    private List<Integer> value = new ArrayList<>();

    public IntListSetting(String path, List<Integer> defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }


    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<Integer> getValue() {
        return value;
    }

    @Override
    public List<Integer> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setValue(List<Integer> value) {
        this.value = value;
    }

    @Override
    public List<Integer> parse(Configuration config, String path) {
        return config.getIntegerList(path);
    }
}


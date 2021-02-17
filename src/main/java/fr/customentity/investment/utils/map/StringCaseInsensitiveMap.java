package fr.customentity.investment.utils.map;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

public class StringCaseInsensitiveMap<U> extends HashMap<String, U> {

    @Override
    public U put(String key, U u) {
       return super.put(StringUtils.capitalize(key.toLowerCase()), u);
    }

    @Override
    public U putIfAbsent(String key, U u) {
        return super.putIfAbsent(StringUtils.capitalize(key.toLowerCase()), u);
    }

    public U get(String key) {
       return super.get(StringUtils.capitalize(key.toLowerCase()));
    }

    public U remove(String key) {
        return super.remove(StringUtils.capitalize(key.toLowerCase()));
    }
}
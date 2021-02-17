package fr.customentity.investment.storage;

import fr.customentity.investment.utils.map.StringCaseInsensitiveMap;

import java.util.*;

public enum StorageType {

    MYSQL("MySQL", "mysql"),
    SQLITE("SQLite", "sqlite");

    private final String name;
    private final String identifier;

    public static final StringCaseInsensitiveMap<StorageType> IDENTIFIER_MAP = new StringCaseInsensitiveMap<>();

    static {
        Arrays.stream(values()).forEach(storageType ->
                IDENTIFIER_MAP.put(storageType.getIdentifier(), storageType));
    }

    StorageType(String name, String identifier) {
        this.name = name;
        this.identifier = identifier;
    }

    public static Optional<StorageType> fromIdentifier(String identifier) {
        return Optional.of(IDENTIFIER_MAP.get(identifier));
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }
}

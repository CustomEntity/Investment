package fr.customentity.investment.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.customentity.investment.InvestmentPlugin;
import fr.customentity.investment.gson.adapters.LocationTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Singleton
public class GsonManager {

    private final InvestmentPlugin plugin;
    private final Gson gson;

    @Inject
    public GsonManager(InvestmentPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .create();
    }

    public Gson getGson() {
        return gson;
    }

    public File getOrCreateFile(String fileName) throws IOException {
        File f = new File(plugin.getDataFolder(), fileName);
        if (!f.exists()) {
            Bukkit.getLogger().info("Creating new file " + fileName + " !");
            f.createNewFile();
        }
        return f;
    }

    public Object fromJson(File f, Type token) throws FileNotFoundException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(f),
                StandardCharsets.UTF_8
        );
        return gson.fromJson(inputStreamReader, token);
    }

    public String toJSON(Object object, Object token) {
        return gson.toJson(object, getTypeFromObject(token));
    }

    public boolean saveJSONToFile(File f, Object toSave, Object token) throws IOException {
        String str = toJSON(toSave, token);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                new FileOutputStream(f),
                StandardCharsets.UTF_8
        );

        outputStreamWriter.write(str);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        return true;
    }

    private Type getTypeFromObject(Object object) {
        return object instanceof Type ? (Type) object : getTypeFromClass(object.getClass());
    }

    private Type getTypeFromClass(Class<?> clazz) {
        return TypeToken.of(clazz).getType();
    }
}

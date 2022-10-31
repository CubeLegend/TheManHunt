package me.CubeLegend.TheManHunt;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Configuration {

    private static Configuration instance;

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    private Configuration() {
        config = TheManHunt.getInstance().getConfig();
    }

    private final FileConfiguration config;

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
}

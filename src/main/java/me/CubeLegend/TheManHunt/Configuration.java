package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.GameModeSystem.GameModeManager;
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
        TheManHunt.getInstance().saveDefaultConfig();
        config = TheManHunt.getInstance().getConfig();
        gmm = new GameModeManager(config.getString("Default.GameMode"));
    }

    private final FileConfiguration config;
    private final GameModeManager gmm;

    public boolean getBoolean(String path) {
        Boolean bool = gmm.getBoolean(path);
        if (bool != null) return bool;
        return config.getBoolean(path);
    }

    public int getInt(String path) {
        Integer integer = gmm.getInt(path);
        if (integer != null) return integer;
        return config.getInt(path);
    }

    public String getString(String path) {
        String string = gmm.getString(path);
        if (string != null) return string;
        return config.getString(path);
    }

    public List<String> getStringList(String path) {
        List<String> stringList = gmm.getStringList(path);
        if (stringList != null) return stringList;
        return config.getStringList(path);
    }
}

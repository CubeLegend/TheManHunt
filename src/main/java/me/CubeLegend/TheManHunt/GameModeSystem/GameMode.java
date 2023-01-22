package me.CubeLegend.TheManHunt.GameModeSystem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GameMode {

    private final File gameModeFile;
    public String name;
    private FileConfiguration gameModeYaml;

    GameMode(GameModeManager gameModeManager, File file) {
        gameModeFile = file;
        createCustomConfig();
        if (!gameModeYaml.getBoolean("GameModeFile")) {
            gameModeManager.removeGameMode(this);
            return;
        }
        String nameFromYaml = gameModeYaml.getString("Name");
        if (nameFromYaml == null) {
            gameModeManager.removeGameMode(this);
            return;
        }
        name = nameFromYaml.toLowerCase();
    }

    private void createCustomConfig() {
        if (!gameModeFile.exists()) {
            gameModeFile.getParentFile().mkdirs();
        }

        gameModeYaml = new YamlConfiguration();
        try {
            gameModeYaml.load(gameModeFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public Boolean getBoolean(String path) {
        if (!gameModeYaml.isBoolean(path)) return null;
        return gameModeYaml.getBoolean(path);
    }

    @Nullable
    public Integer getInt(String path) {
        if (!gameModeYaml.isInt(path)) return null;
        return gameModeYaml.getInt(path);
    }

    public String getString(String path) {
        if (!gameModeYaml.isString(path)) return null;
        return gameModeYaml.getString(path);
    }

    public List<String> getStringList(String path) {
        if (!gameModeYaml.isList(path)) return null;
        return gameModeYaml.getStringList(path);
    }
}

package me.CubeLegend.TheManHunt.GameModeSystem;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class GameMode {

    private final File gameModeFile;
    public String name;
    private FileConfiguration gameModeYaml;

    private final GameModeManager sm = GameModeManager.getInstance();

    GameMode(File file) {
        gameModeFile = file;
        createCustomConfig();
        if (!gameModeYaml.getBoolean("GameModeFile")) {
            sm.removeGameMode(this);
        }
        if (gameModeYaml.getString("Name") == null) {
            sm.removeGameMode(this);
            return;
        }
        name = gameModeYaml.getString("Name").toLowerCase();
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
}

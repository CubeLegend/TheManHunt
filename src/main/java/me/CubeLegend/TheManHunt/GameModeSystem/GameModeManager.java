package me.CubeLegend.TheManHunt.GameModeSystem;

import me.CubeLegend.TheManHunt.Configuration;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.event.Listener;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class GameModeManager implements Listener {

    private static GameModeManager instance;

    public static GameModeManager getInstance() {
        if (instance == null) {
            instance = new GameModeManager();
        }
        return instance;
    }

    private final HashMap<String, GameMode> gameModes = new HashMap<>();

    private String currentGameMode;

    File GameModesDir = new File(TheManHunt.getInstance().getDataFolder(), "game modes");

    GameModeManager() {
        GameModesDir.mkdirs();
        File[] files = GameModesDir.listFiles();

        assert files != null;
        if (files.length == 0) {
            createGameModeFromSource("game modes/classic.yml");
            createGameModeFromSource("game modes/enhanced.yml");
            createGameModeFromSource("game modes/powered_up.yml");
            return;
        }
        for (File file : files) {
            GameMode gameMode = new GameMode(file);
            gameModes.put(gameMode.name, gameMode);
        }

        String defaultGameMode = Configuration.getInstance().getString("Default.GameMode");
        if (defaultGameMode == null) {
            Optional<String> firstKey = gameModes.keySet().stream().findFirst();
            defaultGameMode = firstKey.orElseThrow();
        }
        currentGameMode = defaultGameMode;
    }

    private void createGameModeFromSource(String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = new File(GameModesDir, filename);
        try {
            inputStream = TheManHunt.getInstance().getResource(filename);
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            GameMode gameMode = new GameMode(file);
            gameModes.put(gameMode.name, gameMode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeGameMode(GameMode gm) {
        gameModes.remove(gm.name);
    }

    public List<GameMode> getGameModes() {
        return gameModes.values().stream().toList();
    }

    public boolean getBoolean(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        Boolean boolFromYaml = gm.getBoolean(path);
        if (boolFromYaml == null) {
            // TODO check if in the normal config value exists
            return false;
        }
        return boolFromYaml;
    }

    public int getInt(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        Integer intFromYaml = gm.getInt(path);
        if (intFromYaml == null) {
            // TODO check if in the normal config value exists
            return 0;
        }
        return intFromYaml;
    }
}

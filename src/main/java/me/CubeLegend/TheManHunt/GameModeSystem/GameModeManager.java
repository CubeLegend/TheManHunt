package me.CubeLegend.TheManHunt.GameModeSystem;

import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GameModeManager implements Listener {

    private final HashMap<String, GameMode> gameModes = new HashMap<>();

    private String currentGameMode;

    File GameModesDir = new File(TheManHunt.getInstance().getDataFolder(), "game_modes");

    public GameModeManager(@Nullable String defaultGameMode) {
        GameModesDir.mkdirs();
        File[] files = GameModesDir.listFiles();

        assert files != null;
        if (files.length == 0) {
            createGameModeFromSource("classic.yml");
            createGameModeFromSource("enhanced.yml");
            createGameModeFromSource("powered_up.yml");
        }
        else {
            for (File file : files) {
                GameMode gameMode = new GameMode(this, file);
                gameModes.put(gameMode.name, gameMode);
            }
        }

        if (defaultGameMode == null || !gameModes.containsKey(defaultGameMode)) {
            Bukkit.getLogger().warning("The default game mode doesn't actually exist");
            Optional<String> firstKey = gameModes.keySet().stream().findFirst();
            defaultGameMode = firstKey.orElseThrow();
        }

        for (String name : gameModes.keySet()) {
            if (name.equalsIgnoreCase(defaultGameMode)) {
                currentGameMode = name;
                Bukkit.getScheduler().runTaskLater(
                        TheManHunt.getInstance(),
                        () -> Bukkit.getLogger().info("Game Mode: " + name),
                        1
                        );
                break;
            }
        }
    }

    private void createGameModeFromSource(String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = new File(GameModesDir, filename);
        try {
            inputStream = TheManHunt.getInstance().getResource("game_modes/" + filename);
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while (true) {
                assert inputStream != null;
                if ((read = inputStream.read(bytes)) == -1) break;
                outputStream.write(bytes, 0, read);
            }

            GameMode gameMode = new GameMode(this, file);
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

    public void setGameMode(String gameMode) {
        for (String name : gameModes.keySet()) {
            if (name.equalsIgnoreCase(gameMode)) {
                currentGameMode = name;
                Bukkit.getLogger().info("Game Mode: " + name);
                return;
            }
        }
    }

    public void removeGameMode(GameMode gm) {
        gameModes.remove(gm.name);
    }

    public List<GameMode> getGameModes() {
        return gameModes.values().stream().toList();
    }

    public Boolean getBoolean(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        return gm.getBoolean(path);
    }

    public Integer getInt(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        return gm.getInt(path);
    }

    public String getString(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        return gm.getString(path);
    }

    public List<String> getStringList(String path) {
        GameMode gm = gameModes.get(currentGameMode);
        return gm.getStringList(path);
    }
}

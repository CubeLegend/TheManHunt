package me.CubeLegend.TheManHunt;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.management.ListenerNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataConfig {

    private static DataConfig dataConfig;

    public static DataConfig getInstance() {
        if (dataConfig == null) {
            dataConfig = new DataConfig();
        }
        return dataConfig;
    }

    private File customConfigFile;
    private FileConfiguration customConfig;

    DataConfig() {
        createCustomConfig();
    }

    private void createCustomConfig() {
        customConfigFile = new File(TheManHunt.getInstance().getDataFolder(), "data.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            TheManHunt.getInstance().saveResource("data.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveMembersToYaml(String teamName, List<UUID> members) {
        List<String> sMembers = new ArrayList<>();
        for (UUID uuid : members) {
            sMembers.add(uuid.toString());
        }
        customConfig.set(teamName, sMembers);
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeTeamsFromYaml() {
        List<UUID> empty = new ArrayList<>();
        customConfig.set("Runners", empty);
        customConfig.set("Hunters", empty);
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UUID> loadMembersFromYaml(String teamName) {
        List<UUID> members = new ArrayList<>();
        if (customConfig.isList(teamName)) {
            for (String s : customConfig.getStringList(teamName)) {
                members.add(UUID.fromString(s));
            }
        }
        if (!members.isEmpty()) GameHandler.getInstance().setGameState(GameState.PLAYING);
        return members;
    }

    public void setWorldToDelete(String name) {
        customConfig.set("DeleteWorld", name);
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getWorldToDelete() {
        return customConfig.getString("DeleteWorld");
    }
}

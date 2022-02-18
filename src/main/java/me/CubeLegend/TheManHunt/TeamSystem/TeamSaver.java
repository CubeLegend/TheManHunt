package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamSaver {

    private File customConfigFile;
    private FileConfiguration customConfig;

    TeamSaver() {
        createCustomConfig();
    }

    private void createCustomConfig() {
        customConfigFile = new File(TheManHunt.getInstance().getDataFolder(), "teams.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            TheManHunt.getInstance().saveResource("teams.yml", false);
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

    public List<UUID> loadMembersFromYaml(String teamName) {
        List<UUID> members = new ArrayList<>();
        if (customConfig.isList(teamName)) {
            for (String s : customConfig.getStringList(teamName)) {
                members.add(UUID.fromString(s));
            }
        }
        return members;
    }

    public void deleteSaves() {
        customConfigFile.delete();
    }
}

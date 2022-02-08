package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Team {

    private File customConfigFile;
    private FileConfiguration customConfig;

    private final String teamName;
    private final String teamIcon;
    private final int teamSelectionSlot;
    private final String teamColor;
    private final List<UUID> members = new ArrayList<>();

    public Team(String teamName, String teamIcon, int teamSelectionSlot, String teamColor) {
        this.teamName = teamName;
        this.teamIcon = teamIcon;
        this.teamSelectionSlot = teamSelectionSlot;
        this.teamColor = teamColor;
        createCustomConfig();
        loadMembersFromYaml();
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
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

    private void saveMembersToYaml() {
        List<String> members = new ArrayList<>();
        for (UUID uuid : this.members) {
            members.add(uuid.toString());
        }
        this.getCustomConfig().set(teamName, members);
        try {
            this.getCustomConfig().save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMembersFromYaml() {
        if (this.getCustomConfig().isList(teamName)) {
            for (String s : this.getCustomConfig().getStringList(teamName)) {
                members.add(UUID.fromString(s));
            }
        }
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());

        player.setDisplayName("§" + teamColor + player.getDisplayName() + "§r");
        player.setPlayerListName("§" + teamColor + player.getDisplayName() + "§r");
        saveMembersToYaml();
    }

    public void removeMember(Player player) {
        if (!members.contains(player.getUniqueId())) return;
        if (player.getDisplayName().contains("§")) {
            int index1 = player.getDisplayName().indexOf("§");
            int index2 = player.getDisplayName().indexOf("§r");
            System.out.println(index1);
            System.out.println(index2);
            System.out.println(player.getDisplayName());
            String playerNameWithoutColor = player.getDisplayName().substring(0, index1) + player.getDisplayName().substring(index1 + 2, index2);
            System.out.println(playerNameWithoutColor);
            player.setDisplayName(playerNameWithoutColor);
            player.setPlayerListName(playerNameWithoutColor);
        }
        members.remove(player.getUniqueId());
        saveMembersToYaml();
    }

    public Player getMember(int index) {
        return Bukkit.getPlayer(members.get(index));
    }

    public int getIndexOfMember(Player player) {
        return members.indexOf(player.getUniqueId());
    }

    public List<Player> getMembers() {
        List<Player> membersAsPlayers = new ArrayList<>();
        for (UUID uuid : members) {
            membersAsPlayers.add(Bukkit.getPlayer(uuid));
        }
        return membersAsPlayers;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamIcon() {
        return teamIcon;
    }

    public int getTeamSelectionSlot() {
        return  teamSelectionSlot;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public boolean checkForMember(Player member) {
        return members.contains(member.getUniqueId());
    }

    public int getMemberCount() {
        return members.size();
    }

    public void win() {
        GameHandler.getInstance().setGameState(GameState.END);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(String.format("§6Die %s%s §6haben Gewonnen!!!",teamColor,teamName), null, 10, 70, 20);
        }
        GameHandler.getInstance().connectPlayersToLobby();
    }
}

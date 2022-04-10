package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

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
    private final String teamColorAsCode;
    private final List<UUID> members = new ArrayList<>();

    public Team(String teamName, String teamIcon, int teamSelectionSlot, String teamColor) {
        this.teamName = teamName;
        this.teamIcon = teamIcon;
        this.teamSelectionSlot = teamSelectionSlot;
        this.teamColor = teamColor;
        this.teamColorAsCode = ChatColor.valueOf(teamColor).toString();
        Scoreboard board = TeamHandler.getInstance().getScoreBoard();
        board.registerNewTeam(teamName);
        //Objects.requireNonNull(board.getTeam(teamName)).setPrefix(teamColorAsCode + "Nice"); //doesn't work but should?
        board.getTeam(teamName).setColor(ChatColor.valueOf(teamColor));
        TeamHandler.getInstance().addToTeamOnJoin(teamName, TeamHandler.getInstance().getTeamSaver().loadMembersFromYaml(teamName));
    }

    public void addMember(Player player) {
        if (members.contains(player.getUniqueId())) return;
        TeamHandler.getInstance().removePlayerFromAllTeams(player);
        members.add(player.getUniqueId());
        Objects.requireNonNull(TeamHandler.getInstance().getScoreBoard().getTeam(teamName)).addEntry(player.getName());

        player.setDisplayName(teamColorAsCode + player.getDisplayName() + "§r");
        player.setPlayerListName(teamColorAsCode + player.getDisplayName() + "§r");
        TeamHandler.getInstance().getTeamSaver().saveMembersToYaml(teamName, members);
    }

    public void removeMember(Player player) {
        if (!members.contains(player.getUniqueId())) return;
        if (player.getDisplayName().contains("§")) {
            int index1 = player.getDisplayName().indexOf("§");
            int index2 = player.getDisplayName().indexOf("§r");
            String playerNameWithoutColor = player.getDisplayName().substring(0, index1) + player.getDisplayName().substring(index1 + 2, index2);
            player.setDisplayName(playerNameWithoutColor);
            player.setPlayerListName(playerNameWithoutColor);
        }
        members.remove(player.getUniqueId());
        Objects.requireNonNull(TeamHandler.getInstance().getScoreBoard().getTeam(teamName)).removeEntry(player.getName());
        TeamHandler.getInstance().getTeamSaver().saveMembersToYaml(teamName, members);
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

    public String getTeamColorAsCode() {
        return teamColorAsCode;
    }

    public boolean checkForMember(Player member) {
        return members.contains(member.getUniqueId());
    }

    public int getMemberCount() {
        return members.size();
    }

    public void win() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(String.format("§6Die %s%s §6haben Gewonnen!!!", teamColor, teamName), null, 10, 70, 20);
        }
        GameHandler.getInstance().setGameState(GameState.END);
        TeamHandler.getInstance().getTeamSaver().removeTeamsYaml();
    }
}

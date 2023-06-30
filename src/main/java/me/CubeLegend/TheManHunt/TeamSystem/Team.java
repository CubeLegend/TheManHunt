package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.StateSystem.GameHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.PersistentData.PersistentDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.*;

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
        org.bukkit.scoreboard.Team team = board.registerNewTeam(teamName);
        team.setColor(ChatColor.valueOf(teamColor));
    }

    public void addMember(Player player) {
        if (members.contains(player.getUniqueId())) return;
        TeamHandler.getInstance().removePlayerFromAllTeams(player);
        members.add(player.getUniqueId());
        Objects.requireNonNull(TeamHandler.getInstance().getScoreBoard().getTeam(teamName)).addEntry(player.getName());

        player.setDisplayName(teamColorAsCode + player.getDisplayName() + "§r");
        player.setPlayerListName(teamColorAsCode + player.getDisplayName() + "§r");
    }

    public void addMember(UUID uuid) {
        if (members.contains(uuid)) return;
        TeamHandler.getInstance().removePlayerFromAllTeams(uuid);
        members.add(uuid);
        org.bukkit.scoreboard.Team scoreBoardTeam = Objects.requireNonNull(TeamHandler.getInstance().getScoreBoard().getTeam(teamName));
        Optional.of(Bukkit.getOfflinePlayer(uuid)).map(OfflinePlayer::getName).ifPresent(scoreBoardTeam::addEntry);
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
    }

    public void removeMember(UUID uuid) {
        if (!members.contains(uuid)) return;
        members.remove(uuid);
        org.bukkit.scoreboard.Team scoreBoardTeam = Objects.requireNonNull(TeamHandler.getInstance().getScoreBoard().getTeam(teamName));
        Optional.of(Bukkit.getOfflinePlayer(uuid)).map(OfflinePlayer::getName).ifPresent(scoreBoardTeam::removeEntry);
    }

    public Player getMember(int index) {
        return Bukkit.getPlayer(members.get(index));
    }

    public int getIndexOfMember(UUID uuid) {
        return members.indexOf(uuid);
    }

    public List<Player> getMembers() {
        List<Player> membersAsPlayers = new ArrayList<>();
        for (UUID uuid : members) {
            membersAsPlayers.add(Bukkit.getPlayer(uuid));
        }
        return membersAsPlayers;
    }

    public List<UUID> getMembersRaw() {
        return members;
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
        for (Player player : this.getMembers()) {
            LanguageManager.getInstance().sendTitle(player, Message.TEAM_HAS_WON, Message.TEAM_HAS_WON_SUBTITLE, new String[] {teamName});
        }
        LanguageManager.getInstance().broadcastMessage(Message.BROADCAST_WINNER, new String[] {teamName});
        GameHandler.getInstance().setGameState(GameState.END);
    }

    public void lose() {
        for (Player player : this.getMembers()) {
            LanguageManager.getInstance().sendTitle(player, Message.TEAM_HAS_LOST, Message.TEAM_HAS_LOST_SUBTITLE, new String[] {teamName});
        }
    }
}

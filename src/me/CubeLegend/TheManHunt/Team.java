package me.CubeLegend.TheManHunt;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {

    private final String teamName;
    private final String teamIcon;
    private final int teamSelectionSlot;
    private final String teamColor;
    private final ArrayList<UUID> members = new ArrayList<>();

    public Team(String teamName, String teamIcon, int teamSelectionSlot, String teamColor) {
        this.teamName = teamName;
        this.teamIcon = teamIcon;
        this.teamSelectionSlot = teamSelectionSlot;
        this.teamColor = teamColor;
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());

        player.setDisplayName("§" + teamColor + player.getDisplayName());
        player.setPlayerListName("§" + teamColor + player.getDisplayName());
    }

    public void removeMember(Player player) {
        if (!members.contains(player.getUniqueId())) {
            return;
        }
        if (player.getDisplayName().contains("§")) {
            int index = player.getDisplayName().indexOf("§");
            String playerNameWithoutColor = player.getDisplayName().substring(0, index) + player.getDisplayName().substring(index + 2);
            player.setDisplayName(playerNameWithoutColor);
            player.setPlayerListName(playerNameWithoutColor);
        }
        members.remove(player.getUniqueId());
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
}

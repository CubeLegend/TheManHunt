package me.CubeLegend.TheManHunt.TeamSystem;

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

        player.setDisplayName("§" + teamColor + player.getDisplayName() + "§r");
        player.setPlayerListName("§" + teamColor + player.getDisplayName() + "§r");
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
}

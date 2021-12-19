package me.CubeLegend.TheManHunt.TeamSystem;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TeamHandler {

    private static TeamHandler instance;

    public static TeamHandler getInstance() {
        if (instance == null) {
            instance = new TeamHandler();
        }
        return instance;
    }

    private final LinkedHashMap<String, Team> teams = new LinkedHashMap<>();

    public void createTeam(String teamName, String teamIcon, int teamSelectionSlot, String teamColor) {
        if (teams.containsKey(teamName)) {
            System.out.println("A team with the name " + teamName + " already exists");
            return;
        }
        teams.put(teamName, new Team(teamName, teamIcon, teamSelectionSlot, teamColor));
    }

    public Team getTeam(String teamName) {
        if (!teams.containsKey(teamName)) {
            System.out.println("No team found with the name: " + teamName);
            return null;
        }
        return teams.get(teamName);
    }

    public Team getTeam(int teamNumber) {
        if (!(teams.size() > teamNumber)) {
            System.out.println("Invalid teamNumber: " + teamNumber);
            return null;
        }
        List<Team> teamNumbers = new ArrayList<>(teams.values());
        return teamNumbers.get(teamNumber);
    }

    public int getTotalTeamNumber() {
        return teams.size();
    }

    public void removePlayerFromAllTeams(Player player) {
        TeamHandler.getInstance().getTeam("Hunters").removeMember(player);
        TeamHandler.getInstance().getTeam("Runners").removeMember(player);
        TeamHandler.getInstance().getTeam("Spectators").removeMember(player);
    }

    public int getTotalMemberCount() {
        return this.getTeam("Hunters").getMemberCount() + this.getTeam("Runners").getMemberCount();
    }

    public void deleteTeams() {
        teams.clear();
    }
}

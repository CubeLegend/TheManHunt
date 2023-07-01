package me.CubeLegend.TheManHunt.TeamSystem;

import me.CubeLegend.TheManHunt.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class TeamHandler implements Listener {

    private static TeamHandler instance;

    public static TeamHandler getInstance() {
        if (instance == null) {
            instance = new TeamHandler();
        }
        return instance;
    }

    private final LinkedHashMap<String, Team> teams = new LinkedHashMap<>();

    private Scoreboard scoreBoard;

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

    public List<String> getTeams() {
        return teams.keySet().stream().toList();
    }

    public int getTotalTeamNumber() {
        return teams.size();
    }

    public void removePlayerFromAllTeams(Player player) {
        TeamHandler.getInstance().getTeam("Hunters").removeMember(player);
        TeamHandler.getInstance().getTeam("Runners").removeMember(player);
        TeamHandler.getInstance().getTeam("Spectators").removeMember(player);
    }

    public void removePlayerFromAllTeams(UUID uuid) {
        TeamHandler.getInstance().getTeam("Hunters").removeMember(uuid);
        TeamHandler.getInstance().getTeam("Runners").removeMember(uuid);
        TeamHandler.getInstance().getTeam("Spectators").removeMember(uuid);
    }

    public int getTotalMemberCount() {
        return this.getTeam("Hunters").getMemberCount() + this.getTeam("Runners").getMemberCount();
    }

    public void deleteTeams() {
        teams.clear();
    }

    public Scoreboard getScoreBoard() {
        return scoreBoard;
    }

    public void registerScoreBoard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();

        List<String> teamIcons = new ArrayList<>();
        teamIcons.add(Configuration.getInstance().getString("Teams.Runners.Icon"));
        teamIcons.add(Configuration.getInstance().getString("Teams.Hunters.Icon"));
        teamIcons.add(Configuration.getInstance().getString("Teams.Spectators.Icon"));

        List<Integer> selectionSlots = new ArrayList<>(3);
        selectionSlots.add(Configuration.getInstance().getInt("Teams.Runners.SelectionSlot"));
        selectionSlots.add(Configuration.getInstance().getInt("Teams.Hunters.SelectionSlot"));
        selectionSlots.add(Configuration.getInstance().getInt("Teams.Spectators.SelectionSlot"));
        // check if multiple teams have the same selection slot
        if (selectionSlots.size() != new HashSet<>(selectionSlots).size()) {
            selectionSlots = new ArrayList<>(Arrays.asList(2, 6, 4));
            Bukkit.getLogger().warning("Multiple teams have the same selection slot, using default slots instead");
        }

        List<String> teamColors = new ArrayList<>();
        teamColors.add(Configuration.getInstance().getString("Teams.Runners.Color"));
        teamColors.add(Configuration.getInstance().getString("Teams.Hunters.Color"));
        teamColors.add(Configuration.getInstance().getString("Teams.Spectators.Color"));

        TeamHandler.getInstance().createTeam("Runners", teamIcons.get(0).toUpperCase(),
                selectionSlots.get(0), teamColors.get(0).toUpperCase());
        TeamHandler.getInstance().createTeam("Hunters", teamIcons.get(1).toUpperCase(),
                selectionSlots.get(1), teamColors.get(1).toUpperCase());
        TeamHandler.getInstance().createTeam("Spectators", teamIcons.get(2).toUpperCase(),
                selectionSlots.get(2), teamColors.get(2).toUpperCase());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //TODO check if name color prefix is correct
        event.getPlayer().setScoreboard(scoreBoard);
    }
}

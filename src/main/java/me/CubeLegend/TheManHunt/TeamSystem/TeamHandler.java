package me.CubeLegend.TheManHunt.TeamSystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
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

    private ScoreboardManager manager;
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

    public int getTotalMemberCount() {
        return this.getTeam("Hunters").getMemberCount() + this.getTeam("Runners").getMemberCount();
    }

    public void deleteTeams() {
        teams.clear();
    }

    public Scoreboard getScoreBoard() {
        return scoreBoard;
    }

    private HashMap<UUID, String>  playersToAdd = new HashMap<>();

    public void addToTeamOnJoin(String teamName, List<UUID> membersToAdd) {
        for (UUID uuid : membersToAdd) {
            playersToAdd.put(uuid, teamName);
        }
    }

    private int worldCount = 0;

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (++worldCount >= 3) {
            manager = Bukkit.getScoreboardManager();
            assert manager != null;
            scoreBoard = manager.getNewScoreboard();
            TeamHandler.getInstance().createTeam("Runners", "diamond_shovel", 3, "BLUE");
            TeamHandler.getInstance().createTeam("Hunters", "diamond_sword", 6, "RED");
            TeamHandler.getInstance().createTeam("Spectators", "gray_stained_glass", 5, "GRAY");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(scoreBoard);
        if (playersToAdd.containsKey(event.getPlayer().getUniqueId())) {
            getTeam(playersToAdd.get(event.getPlayer().getUniqueId())).addMember(event.getPlayer());
        }
    }
}

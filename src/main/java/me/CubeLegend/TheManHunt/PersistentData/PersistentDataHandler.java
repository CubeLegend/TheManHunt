package me.CubeLegend.TheManHunt.PersistentData;

import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public class PersistentDataHandler implements Listener {

    private static PersistentDataHandler persistentDataHandler;

    public static PersistentDataHandler getInstance() {
        if (persistentDataHandler == null) {
            persistentDataHandler = new PersistentDataHandler();
        }
        return persistentDataHandler;
    }

    private final PersistentData persistentData;
    private boolean persistentDataChanged;
    private final String filePathData = new File(TheManHunt.getInstance().getDataFolder(), "data.gz").getPath();

    private final PersistentStats persistentStats;
    private boolean persistentStatsChanged;
    private final String filePathStats = new File(TheManHunt.getInstance().getDataFolder(), "stats.gz").getPath();

    private PersistentDataHandler() {
        if (new File(filePathData).exists()) {
            persistentData = PersistentData.loadData(filePathData);
        } else {
            persistentData = new PersistentData("", Collections.emptyList(), Collections.emptyList());
            persistentData.saveData(filePathData);
        }

        if (new File(filePathStats).exists()) {
            persistentStats = PersistentStats.loadData(filePathStats);
        } else {
            persistentStats = new PersistentStats(0, 0);
            persistentStats.saveData(filePathStats);
        }
    }

    public void saveData() {
        if (persistentDataChanged) {
            persistentData.saveData(filePathData);
            persistentDataChanged = false;
        }

        if (persistentStatsChanged) {
            persistentStats.saveData(filePathStats);
            persistentStatsChanged = false;
        }
    }

    public void logContent() {
        Logger l = Bukkit.getLogger();
        String indentation = "  ";

        l.info("PersistentData:");
        for (Field field : persistentData.getClass().getFields()) {
            try {
                l.info(indentation + field.getName() + ": " + field.get(persistentData));
            } catch (IllegalAccessException e) {
                l.warning("Could not get Value of " + field.getName());
            }
        }

        l.info("PersistentStats:");
        for (Field field : persistentStats.getClass().getFields()) {
            try {
                l.info(indentation + field.getName() + ": " + field.get(persistentStats));
            } catch (IllegalAccessException e) {
                l.warning("Could not get Value of " + field.getName());
            }
        }

        /*
        Bukkit.getLogger().info("Persistent Data: ");
        Bukkit.getLogger().info("   deleteWorldOnStartUp: " + deleteWorldOnStartUp);
        Bukkit.getLogger().info("   runners: " + runners);
        Bukkit.getLogger().info("   hunters: " + hunters);
        Bukkit.getLogger().info("   allRunnerWins: " + allRunnerWins);
        Bukkit.getLogger().info("   allHunterWins: " + allHunterWins);
         */
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() == GameState.IDLE) {
            if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
                persistentData.deleteWorldOnStartUp = "";
                persistentData.runners = TeamHandler.getInstance().getTeam("Runners").getMembersRaw();
                persistentData.hunters = TeamHandler.getInstance().getTeam("Hunters").getMembersRaw();
                persistentData.saveData(filePathData);
                System.out.println(getRunners());
                this.logContent();
            }
        }
        else if (event.getChangeFrom() == GameState.PLAYING) {
            if (event.getChangeTo() == GameState.END) {
                persistentData.deleteWorldOnStartUp = Bukkit.getWorlds().get(0).getName();
                persistentData.runners = Collections.emptyList();
                persistentData.hunters = Collections.emptyList();
                persistentData.saveData(filePathData);
                this.logContent();
            }
        }
    }

    public String getDeleteWorldOnStartUp() {
        return persistentData.deleteWorldOnStartUp;
    }

    public void setDeleteWorldOnStartUp(String deleteWorldOnStartUp) {
        if (Objects.equals(deleteWorldOnStartUp, persistentData.deleteWorldOnStartUp)) return;
        persistentData.deleteWorldOnStartUp = deleteWorldOnStartUp;
        persistentDataChanged = true;
    }

    public List<UUID> getRunners() {
        return persistentData.runners;
    }

    public void setRunners(List<UUID> runners) {
        if (Objects.equals(runners, persistentData.runners)) return;
        persistentData.runners = runners;
        persistentDataChanged = true;
    }

    public List<UUID> getHunters() {
        return persistentData.hunters;
    }

    public void setHunters(List<UUID> hunters) {
        if (Objects.equals(hunters, persistentData.hunters)) return;
        persistentData.hunters = hunters;
        persistentDataChanged = false;
    }

    public int getAllRunnerWins() {
        return persistentStats.allRunnerWins;
    }

    public void setAllRunnerWins(int allRunnerWins) {
        if (allRunnerWins == persistentStats.allRunnerWins) return;
        persistentStats.allRunnerWins = allRunnerWins;
        persistentStatsChanged = true;
    }

    public int getAllHunterWins() {
        return persistentStats.allHunterWins;
    }

    public void setAllHunterWins(int allHunterWins) {
        if (allHunterWins == persistentStats.allHunterWins) return;
        persistentStats.allHunterWins = allHunterWins;
        persistentStatsChanged = true;
    }
}

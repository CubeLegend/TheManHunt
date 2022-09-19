package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PersistentDataHandler implements Listener {

    private static PersistentDataHandler persistentDataHandler;

    public static PersistentDataHandler getInstance() {
        if (persistentDataHandler == null) {
            persistentDataHandler = new PersistentDataHandler();
        }
        return persistentDataHandler;
    }

    private final String filePath = new File(TheManHunt.getInstance().getDataFolder(), "data.gz").getPath();

    public String deleteWorldOnStartUp;

    public List<UUID> runners;
    public List<UUID> hunters;

    public int allRunnerWins;
    public int allHunterWins;

    public PersistentDataHandler() {
        loadData();
    }

    private void loadData() {
        if (new File(filePath).exists()) {
            if (PersistentData.loadData(filePath) != null) {
                PersistentData data = new PersistentData(Objects.requireNonNull(PersistentData.loadData(filePath)));
                this.deleteWorldOnStartUp = data.deleteWorldOnStartUp;

                this.runners = data.runners;
                this.hunters = data.hunters;

                this.allRunnerWins = data.allRunnerWins;
                this.allHunterWins = data.allHunterWins;
                return;
            }
        }
        this.deleteWorldOnStartUp = "";

        this.runners = Collections.emptyList();
        this.hunters = Collections.emptyList();

        this.allRunnerWins = 0;
        this.allHunterWins = 0;
    }

    public void saveData() {
        new PersistentData(
                deleteWorldOnStartUp,
                runners,
                hunters,
                allRunnerWins,
                allHunterWins
        ).saveData(new File(TheManHunt.getInstance().getDataFolder(), "data.gz").getPath());
    }

    public void logContent() {
        Bukkit.getLogger().info("Persistent Data: ");
        Bukkit.getLogger().info("   deleteWorldOnStartUp: " + deleteWorldOnStartUp);
        Bukkit.getLogger().info("   runners: " + runners);
        Bukkit.getLogger().info("   hunters: " + hunters);
        Bukkit.getLogger().info("   allRunnerWins: " + allRunnerWins);
        Bukkit.getLogger().info("   allHunterWins: " + allHunterWins);
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() == GameState.IDLE) {
            if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
                this.deleteWorldOnStartUp = "";
                this.runners = TeamHandler.getInstance().getTeam("Runners").getMembersRaw();
                this.hunters = TeamHandler.getInstance().getTeam("Hunters").getMembersRaw();
                this.saveData();
                this.logContent();
            }
        }
        else if (event.getChangeFrom() == GameState.PLAYING) {
            if (event.getChangeTo() == GameState.END) {
                this.deleteWorldOnStartUp = Bukkit.getWorlds().get(0).getName();
                this.runners = Collections.emptyList();
                this.hunters = Collections.emptyList();
                this.saveData();
                this.logContent();
            }
        }
    }
}

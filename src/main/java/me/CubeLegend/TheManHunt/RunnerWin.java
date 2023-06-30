package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.PersistentData.PersistentDataHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class RunnerWin implements Listener {

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!(GameHandler.getInstance().getGameState() == GameState.RUNAWAYTIME || GameHandler.getInstance().getGameState() == GameState.PLAYING)) return;
        if (!TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) return;
        if (event.getFrom().getEnvironment().equals(Environment.THE_END)) {
            PersistentDataHandler pdh = PersistentDataHandler.getInstance();
            pdh.setAllRunnerWins(pdh.getAllRunnerWins() + 1);
            pdh.saveData();

            TeamHandler.getInstance().getTeam("Runners").win();
            TeamHandler.getInstance().getTeam("Hunters").lose();
        }
    }
}
package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
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
            RunnerWinGame();
        }
    }

    public void RunnerWinGame() {
        GameHandler.getInstance().setGameState(GameState.END);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§6Die §1Runner §6haben Gewonnen!!!", null, 10, 70, 20);
        }
        GameHandler.getInstance().connectPlayersToLobby();
    }
}
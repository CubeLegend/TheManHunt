package me.CubeLegend.TheManHunt;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.CubeLegend.TheManHunt.Compass.CompassSpinning;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.io.File;
import java.util.Arrays;

public class RunnerWin implements Listener {

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!TeamHandler.getInstance().getTeam("Runners").checkForMember(player)) return;
        if (event.getFrom().getEnvironment().equals(Environment.THE_END)) {
            RunnerWinGame();
        }
    }

    public void RunnerWinGame() {
        //GameHandler.getInstance().setGameState(GameState.END);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("§6Die §1Runner §6haben Gewonnen!!!", null, 10, 70, 20);
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {
            Freeze.getInstance().stopFreezeVisionRoutine();

            //HunterNearWarning.getInstance().cancelHunterNearWarning();
            RunnerTracker.getInstance().stopRunnerTrackerRoutine();
            VillageTracker.getInstance().stopVillageTrackingRoutine();
            CompassSpinning.getInstance().stopSpinningCompassRoutine();

            TeamHandler.getInstance().deleteTeams();
            Freeze.getInstance().clear();

            //connect players to lobby
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("lobby");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendPluginMessage(TheManHunt.getInstance(), "BungeeCord", out.toByteArray());
            }
            //remove Player data
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {
                for (World world : Bukkit.getWorlds()) {
                    File[] files = new File(world.getWorldFolder().getAbsolutePath() + "/playerdata/").listFiles();
                    System.out.println(Arrays.toString(files));
                    if (files != null) {
                        for (File file : files) {
                            System.out.println(file.delete());
                        }
                    }
                }
            }, 20);
        }, 15*20);
    }
}
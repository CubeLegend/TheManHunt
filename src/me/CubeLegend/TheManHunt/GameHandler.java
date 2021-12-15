package me.CubeLegend.TheManHunt;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.CubeLegend.TheManHunt.Compass.CompassSpinning;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;

public class GameHandler {
	
	private static GameHandler gameHandler;

	public static GameHandler getInstance() {
		if (gameHandler == null) {
			gameHandler = new GameHandler();
		}
		return gameHandler;
	}
	
	private GameState state;
	
	public GameState getGameState() {
		return state;
	}
	
	public void setGameState(GameState gameState) {
		state = gameState;
		if (state == GameState.PLAYING) {
			Freeze.getInstance().addPlayersToVision("Runners");
		}
	}
	
	public void connectPlayersToLobby() {
		if (GameHandler.getInstance().getGameState() == GameState.END) {
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
					GameHandler.getInstance().setGameState(GameState.IDLE);
				}, 20);
			}, 15*20);
		}
	}

	public void checkAllTeamsSetup() {
		if (TeamHandler.getInstance().getTeam("Runners") == null || TeamHandler.getInstance().getTeam("Hunters") == null) return;
		if (TeamHandler.getInstance().getTeam("Runners").getMemberCount() <= 0
				&& TeamHandler.getInstance().getTeam("Hunters").getMemberCount() <= 0) {
			return;
		}
		if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
			HunterWaitTimer.getInstance().startTimer();
		}
	}
}

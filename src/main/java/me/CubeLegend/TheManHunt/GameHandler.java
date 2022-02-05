package me.CubeLegend.TheManHunt;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

		if (state == GameState.IDLE) {
			return;
		}

		if (state == GameState.RUNAWAYTIME) {
			Freeze.getInstance().addFrozenPlayers(TeamHandler.getInstance().getTeam("Hunters").getMembers());
			HunterWaitTimer.getInstance().startTimer();
			if (Settings.getInstance().FreezeVision) {
				for (Player runner : TeamHandler.getInstance().getTeam("Runners").getMembers()) {
					FreezeVision.getInstance().givePlayerFreezeVision(runner);
				}
			}
			return;
		}

		if (state == GameState.PLAYING) {
			return;
		}

		if (state == GameState.END) {
			if (Settings.getInstance().FreezeVision) {
				for (Player runner : TeamHandler.getInstance().getTeam("Runners").getMembers()) {
					FreezeVision.getInstance().takePlayerFreezeVision(runner);
				}
			}
			connectPlayersToLobby();
			//Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), this::resetWorld, 20*20);
		}
	}

	public void connectPlayersToLobby () {
		if (GameHandler.getInstance().getGameState() == GameState.END) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {

				//HunterNearWarning.getInstance().cancelHunterNearWarning();
				RunnerTracker.getInstance().stopRunnerTrackerRoutine();
				VillageTracker.getInstance().stopVillageTrackingRoutine();

				TeamHandler.getInstance().deleteTeams();

				//connect players to lobby
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF("lobby");
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.sendPluginMessage(TheManHunt.getInstance(), "BungeeCord", out.toByteArray());
				}
				//remove Player data
//				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {
//					for (World world : Bukkit.getWorlds()) {
//						File[] files = new File(world.getWorldFolder().getAbsolutePath() + "/playerdata/").listFiles();
//						System.out.println(Arrays.toString(files));
//						if (files != null) {
//							for (File file : files) {
//								System.out.println(file.delete());
//							}
//						}
//					}
//					GameHandler.getInstance().setGameState(GameState.IDLE);
//				}, 20);
			}, 15 * 20);
		}
	}

	public void resetWorld() {
		if (GameHandler.getInstance().getGameState() != GameState.END) {
			return;
		}
		List<File> worldFiles = new ArrayList<>();
		for (World world : Bukkit.getWorlds()) {
			for (Player player : world.getPlayers()) {
				player.kickPlayer("You should have been connected to another server already");
			}
			worldFiles.add(world.getWorldFolder().getAbsoluteFile());
			System.out.println(Bukkit.getServer().unloadWorld(world, false));
		}
		for (File file : worldFiles) {
			System.out.println(file);
			System.out.println(file.delete());
		}
		Bukkit.shutdown();
	}

	public void checkAllTeamsSetup () {
		if (TeamHandler.getInstance().getTeam("Runners") == null || TeamHandler.getInstance().getTeam("Hunters") == null)
			return;
		if (TeamHandler.getInstance().getTeam("Runners").getMemberCount() <= 0
				&& TeamHandler.getInstance().getTeam("Hunters").getMemberCount() <= 0) {
			return;
		}
		if (GameHandler.getInstance().getGameState() == GameState.IDLE) {
			HunterWaitTimer.getInstance().startTimer();
		}
	}
}

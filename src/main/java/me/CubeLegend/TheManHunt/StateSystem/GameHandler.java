package me.CubeLegend.TheManHunt.StateSystem;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.CubeLegend.TheManHunt.*;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.SpecialAbilities.HunterNearWarning;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TeamSystem.TeamSelectionItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
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
		Bukkit.getLogger().info("Game State: " + state.name());

		if (state == GameState.IDLE) {
			return;
		}

		if (state == GameState.RUNAWAYTIME) {
			PersistentDataHandler data = PersistentDataHandler.getInstance();
			data.deleteWorldOnStartUp = "";
			data.runners = TeamHandler.getInstance().getTeam("Runners").getMembersRaw();
			data.hunters = TeamHandler.getInstance().getTeam("Hunters").getMembersRaw();
			data.saveData();
			data.logContent();

			for (Player player : Bukkit.getOnlinePlayers()) {
				TeamSelectionItem.getInstance().removeFromPlayer(player);
			}

			HunterCatchUp hp = new HunterCatchUp();
			hp.startHunterCatchUpRoutine(10);
			Freeze.getInstance().addFrozenPlayers(TeamHandler.getInstance().getTeam("Hunters").getMembersRaw());
			HunterWaitTimer.getInstance().startTimer();
			if (Settings.getInstance().FreezeVision) {
				for (Player runner : TeamHandler.getInstance().getTeam("Runners").getMembers()) {
					FreezeVision.getInstance().givePlayerFreezeVision(runner);
				}
			}
			if (Settings.getInstance().RunnerTracker) {
				List<Player> hunters = TeamHandler.getInstance().getTeam("Hunters").getMembers();
				for (Player hunter : hunters) {
					RunnerTracker.getInstance().giveToPlayer(hunter);
				}
			}
			if (Settings.getInstance().VillageTracker) {
				List<Player> runners = TeamHandler.getInstance().getTeam("Runners").getMembers();
				for (Player runner : runners) {
					VillageTracker.getInstance().giveToPlayer(runner);
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
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), Bukkit::shutdown, 10*20);
			PersistentDataHandler data = PersistentDataHandler.getInstance();
			data.deleteWorldOnStartUp = Bukkit.getWorlds().get(0).getName();
			data.runners = Collections.emptyList();
			data.hunters = Collections.emptyList();
			data.saveData();
			data.logContent();
		}
	}

	public void connectPlayersToLobby () {
		if (GameHandler.getInstance().getGameState() == GameState.END) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {

				HunterNearWarning.getInstance().stopRoutine();
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

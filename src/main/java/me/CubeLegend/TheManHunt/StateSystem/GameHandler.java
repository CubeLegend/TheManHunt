package me.CubeLegend.TheManHunt.StateSystem;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.CubeLegend.TheManHunt.*;
import me.CubeLegend.TheManHunt.Compass.FortressTracker;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.SpecialAbilities.HunterNearWarning;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GameHandler {

	private static GameHandler gameHandler;

	public static GameHandler getInstance() {
		if (gameHandler == null) {
			gameHandler = new GameHandler();
		}
		return gameHandler;
	}

	private GameState state = GameState.IDLE;

	public GameState getGameState() {
		return state;
	}

	public void setGameState(GameState gameState) {
		GameStateChangeEvent gtce = new GameStateChangeEvent(this.state, gameState);
		Bukkit.getPluginManager().callEvent(gtce);
		state = gtce.getChangeTo();
		Bukkit.getLogger().info("Game State: " + state.name());

		if (state == GameState.END) {

			connectPlayersToLobby();
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), Bukkit::shutdown, 10*20);
		}
	}

	public void connectPlayersToLobby () {
		if (GameHandler.getInstance().getGameState() == GameState.END) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TheManHunt.getInstance(), () -> {

				HunterNearWarning.getInstance().stopRoutine();
				FortressTracker.getInstance().stopFortressTrackingRoutine();
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

package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;

public class HunterWaitTimer {

	private static HunterWaitTimer hunterWaitTimer;

	public static HunterWaitTimer getInstance() {
		if (hunterWaitTimer == null) {
			hunterWaitTimer = new HunterWaitTimer();
		}
		return hunterWaitTimer;
	}

	private int TaskID;

	private static int time;

	private boolean isRunning = false;

	public void startTimer() {

		if (isRunning) { return; }
		isRunning = true;
		time = Settings.getInstance().HunterWaitTimer;
		TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TheManHunt.getInstance(), () -> {

			if (time <= 0) {

				Bukkit.broadcastMessage("§6The hunters got released!");
				Freeze.getInstance().removeFrozenPlayers(TeamHandler.getInstance().getTeam("Hunters").getMembers());
				DataConfig.getInstance().saveCustomConfig();
				GameHandler.getInstance().setGameState(GameState.PLAYING);
				Bukkit.getScheduler().cancelTask(TaskID);
			}

			if (time % 10 == 0 && time >= 10 || time <= 5 && time != 0) {

				Bukkit.broadcastMessage("§6The hunters get released in §1" + time + " §6seconds");
			}

			time--;
		}, 0, 20);
	}

	public void stopTimer() {
		if (Bukkit.getScheduler().isCurrentlyRunning(TaskID)) {
			Bukkit.getScheduler().cancelTask(TaskID);
		}
	}
}

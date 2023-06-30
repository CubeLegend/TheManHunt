package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.Configuration;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.*;

public class RunnerTracker extends Tracker {
	
	private static RunnerTracker instance;

	public static RunnerTracker getInstance() {
		if (instance == null) {
			instance = new RunnerTracker();
		}
		return instance;
	}

	public RunnerTracker() {
        super(
                "Runner Tracker",
                Material.COMPASS,
                TeamHandler.getInstance().getTeam("Hunters"),
                "Abilities.Hunter.RunnerTracker"
        );
	}
	
	//Hunter, Runner
	private final HashMap<UUID, UUID> playerTracking = new HashMap<>();

	public void updatePlayerTracking(Player hunter, Player runner) {
		if (playerTracking.containsKey(hunter.getUniqueId())) {
			playerTracking.replace(hunter.getUniqueId(), runner.getUniqueId());
		} else {
			playerTracking.put(hunter.getUniqueId(), runner.getUniqueId());
		}
	}

	int TaskId;

	private final Configuration config = Configuration.getInstance();

	public void startRunnerTrackerRoutine(int period) {
		TaskId = Bukkit.getServer().getScheduler().runTaskTimer(TheManHunt.getInstance(), () -> {

			for (UUID uuid : playerTracking.keySet()) {
				Player hunter = Bukkit.getPlayer(uuid);
				if (hunter == null) continue;
				Player runner = Bukkit.getPlayer(playerTracking.get(uuid));
				if (runner != null && runner.getWorld().equals(hunter.getWorld())) {

					if (hunter.getWorld().getEnvironment() != World.Environment.NORMAL
							|| config.getBoolean("AlwaysSetLodestone")) {
						setLodestone(hunter, runner);
					} else {
						setTarget(hunter, runner);
					}
				}
			}

		}, 0, period).getTaskId();
	}

	private void setTarget(Player hunter, Player runner) {
		ItemStack runnerTracker = findRunnerTracker(hunter);
		if (runnerTracker != null && ((CompassMeta) Objects.requireNonNull(runnerTracker.getItemMeta())).hasLodestone()) {
			runnerTracker.setItemMeta(this.getItem().getItemMeta());
		}
		hunter.setCompassTarget(runner.getLocation());
	}

	void setLodestone(Player hunter, Player runner) {
		ItemStack runnerTracker = findRunnerTracker(hunter);
		if (runnerTracker != null) {
			CompassMeta cm = (CompassMeta) runnerTracker.getItemMeta();
			Location runnerLoc = runner.getLocation().getBlock().getLocation();
			assert cm != null;
			if (config.getBoolean("AlwaysUpdateRunnerTracker")) {
				if (runnerLoc.equals(cm.getLodestone())) {
					runnerLoc = runnerLoc.add(0, 10, 0);
				}
			}
			cm.setLodestone(runnerLoc);
			runnerTracker.setItemMeta(cm);
		}
	}

	public void stopRunnerTrackerRoutine() {
		if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
			Bukkit.getScheduler().cancelTask(TaskId);
		}
	}

	private ItemStack findRunnerTracker(Player player) {
		for (ItemStack is : player.getInventory()) {
			if (this.getItem().equals(is)) {
				return is;
			}
		}
		return null;
	}

	@Override
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (!event.hasItem()) return;
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
		if (!Objects.equals(event.getItem(), this.getItem())) return;

		if (event.getClickedBlock() != null) {
			if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
				event.setCancelled(true);
				return;
			}
		}

		Player player = event.getPlayer();
		if (!playerTracking.containsKey(player.getUniqueId())) {
			RunnerTracker.getInstance().updatePlayerTracking(player, TeamHandler.getInstance().getTeam("Runners").getMember(0));
		}

		UUID currentRunner = playerTracking.get(player.getUniqueId());
		int nextIndex = TeamHandler.getInstance().getTeam("Runners").getIndexOfMember(currentRunner) + 1;
		if (nextIndex >= TeamHandler.getInstance().getTeam("Runners").getMemberCount()) nextIndex = 0;
		Player runner = TeamHandler.getInstance().getTeam("Runners").getMember(nextIndex);
		RunnerTracker.getInstance().updatePlayerTracking(player, runner);
		LanguageManager.getInstance().sendMessage(player, Message.COMPASS_POINTS_TO, new String[] {runner.getDisplayName()});
	}
}

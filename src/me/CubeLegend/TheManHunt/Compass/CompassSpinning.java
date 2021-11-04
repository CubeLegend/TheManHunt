package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class CompassSpinning implements Listener {
	
	private static CompassSpinning instance;

	public static CompassSpinning getInstance() {
		if (instance == null) {
			instance = new CompassSpinning();
		}
		return instance;
	}

	public ArrayList<UUID> spinRunnerTracker = new ArrayList<UUID>();

	public boolean containsSpinPlayerCompass(Player player) {
		return spinRunnerTracker.contains(player.getUniqueId());
	}

	public void addSpinPlayerCompass(Player player) {
		if (!spinRunnerTracker.contains(player.getUniqueId())) {
		spinRunnerTracker.add(player.getUniqueId());
		}
	}
	
	public void removeSpinPlayerCompass(Player player) {
		if (spinRunnerTracker.contains(player.getUniqueId())) {
			spinRunnerTracker.remove(player.getUniqueId());
		}
	}
	
	//Removing player from ArrayList when player leaves ---
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (spinRunnerTracker.contains(event.getPlayer().getUniqueId())) {
			spinRunnerTracker.remove(event.getPlayer().getUniqueId());
		}
	}
	//-----------------------------------------------------

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld() != Objects.requireNonNull(event.getTo()).getWorld()) {
			if (Objects.requireNonNull(event.getTo().getWorld()).getEnvironment() == World.Environment.NORMAL) {
				for (ItemStack is : event.getPlayer().getInventory().getContents()) {
					if (is == null) continue;
					if (is.getType() == Material.COMPASS
							&& Objects.requireNonNull(is.getItemMeta()).getDisplayName().equals(Objects.requireNonNull(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta()).getDisplayName())
							&& is.getItemMeta().isUnbreakable() == Objects.requireNonNull(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta()).isUnbreakable()) {
						is.setItemMeta(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta());
						break;
					}
				}
			}
		}
	}
	
	private static int TaskId = 4;
	private static int counter = 1;
	
	private static final Location loc1 = new Location(null, 1000, 50, 0);
	private static final Location loc2 = new Location(null, 1000, 50, 1000);
	private static final Location loc3 = new Location(null, 0, 50, 1000);
	private static final Location loc4 = new Location(null, -1000, 50, 1000);
	private static final Location loc5 = new Location(null, -1000, 50, 0);
	private static final Location loc6 = new Location(null, -1000, 50, -1000);
	private static final Location loc7 = new Location(null, 0, 50, -1000);
	private static final Location loc8 = new Location(null, 1000, 50, -1000);

	public void startSpinningCompassRoutine(int period) {
		
		TaskId = Bukkit.getScheduler().runTaskTimer(TheManHunt.getInstance(), () -> {
			ArrayList<UUID> toRemove = new ArrayList<>();
			for (UUID uuid : spinRunnerTracker) {
				if (Bukkit.getPlayer(uuid) != null) {
					Player player = Bukkit.getPlayer(uuid);

					assert player != null;
					loc1.setWorld(player.getWorld());
					loc2.setWorld(player.getWorld());
					loc3.setWorld(player.getWorld());
					loc4.setWorld(player.getWorld());
					loc5.setWorld(player.getWorld());
					loc6.setWorld(player.getWorld());
					loc7.setWorld(player.getWorld());
					loc8.setWorld(player.getWorld());

					ItemStack compassItem = null;
					CompassMeta compassMeta = null;
					for (ItemStack is : player.getInventory().getContents()) {
						if (is == null) continue;
						if (is.getType() == Material.COMPASS
								&& (Objects.requireNonNull(is.getItemMeta()).getDisplayName().equals(Objects.requireNonNull(VillageTracker.getInstance().getVillageTrackerItem().getItemMeta()).getDisplayName())
								|| Objects.requireNonNull(is.getItemMeta()).getDisplayName().equals(Objects.requireNonNull(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta()).getDisplayName()))
								&& (is.getItemMeta().isUnbreakable() == VillageTracker.getInstance().getVillageTrackerItem().getItemMeta().isUnbreakable()
								|| is.getItemMeta().isUnbreakable() == RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta().isUnbreakable())) {
							compassItem = is;
							compassMeta = (CompassMeta) is.getItemMeta();
							break;
						}
					}
					if (compassItem != null) {
						if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
							if (counter == 1) player.setCompassTarget(player.getLocation().add(loc1));
							if (counter == 2) player.setCompassTarget(player.getLocation().add(loc2));
							if (counter == 3) player.setCompassTarget(player.getLocation().add(loc3));
							if (counter == 4) player.setCompassTarget(player.getLocation().add(loc4));
							if (counter == 5) player.setCompassTarget(player.getLocation().add(loc5));
							if (counter == 6) player.setCompassTarget(player.getLocation().add(loc6));
							if (counter == 7) player.setCompassTarget(player.getLocation().add(loc7));
							if (counter == 8) player.setCompassTarget(player.getLocation().add(loc8));
						} else {
							assert compassMeta != null;
							if (counter == 1) compassMeta.setLodestone(player.getLocation().add(loc1));
							if (counter == 2) compassMeta.setLodestone(player.getLocation().add(loc2));
							if (counter == 3) compassMeta.setLodestone(player.getLocation().add(loc3));
							if (counter == 4) compassMeta.setLodestone(player.getLocation().add(loc4));
							if (counter == 5) compassMeta.setLodestone(player.getLocation().add(loc5));
							if (counter == 6) compassMeta.setLodestone(player.getLocation().add(loc6));
							if (counter == 7) compassMeta.setLodestone(player.getLocation().add(loc7));
							if (counter == 8) compassMeta.setLodestone(player.getLocation().add(loc8));
							compassMeta.setLodestoneTracked(false);
							compassItem.setItemMeta(compassMeta);
						}
						continue;
					}
					toRemove.add(uuid);
				}
			}
			for (UUID uuid : toRemove) {
				spinRunnerTracker.remove(uuid);
			}
			if (counter >= 8) {
				counter = 1;
			} else {
				counter++;
			}
		}, 0, period).getTaskId();
	}
	
	public void stopSpinningCompassRoutine() {
		Bukkit.getScheduler().cancelTask(TaskId);
	}
}

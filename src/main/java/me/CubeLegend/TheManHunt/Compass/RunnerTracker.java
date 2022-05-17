package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.Settings;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RunnerTracker implements Listener {
	
	private static RunnerTracker instance;

	public static RunnerTracker getInstance() {
		if (instance == null) {
			instance = new RunnerTracker();
		}
		return instance;
	}
	
	//Hunter, Runner
	private final HashMap<UUID, UUID> PlayerTracking = new HashMap<>();

	private final ArrayList<UUID> returnTrackerToPlayer = new ArrayList<>();

	public ItemStack getRunnerTrackerItem() {
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		
	    ItemMeta meta = compass.getItemMeta();
		assert meta != null;
		meta.setDisplayName("Runner Tracker");
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	    meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
	    compass.setItemMeta(meta);
	    return compass;
	}

	public void givePlayerRunnerTracker(Player player) {
		ItemStack compass = getRunnerTrackerItem();
	    
	    player.getInventory().addItem(compass);
	}

	public boolean isRunnerTracker(ItemStack is) {
		if (is == null) return false;
		if (is.getType() != Material.COMPASS) return false;
		if (is.getItemMeta() == null) return false;
		if (!is.getItemMeta().getDisplayName().equals(Objects.requireNonNull(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta()).getDisplayName())) return false;
		if (!(is.getItemMeta().isUnbreakable() == Objects.requireNonNull(this.getRunnerTrackerItem().getItemMeta()).isUnbreakable())) return false;

		return true;
	}

	public boolean containsPlayerTrackingKey(Player hunter) {
		return PlayerTracking.containsKey(hunter.getUniqueId());
	}
	
	public Player getPlayerTrackingValue(Player hunter) {
		if (PlayerTracking.containsKey(hunter.getUniqueId())) {
			return Bukkit.getPlayer(PlayerTracking.get(hunter.getUniqueId()));
		} else {
			return null;
		}
	}

	public void updatePlayerTracking(Player hunter, Player runner) {
		if (PlayerTracking.containsKey(hunter.getUniqueId())) {
			PlayerTracking.replace(hunter.getUniqueId(), runner.getUniqueId());
		} else {
			PlayerTracking.put(hunter.getUniqueId(), runner.getUniqueId());
		}
	}

	int TaskId = 0;

	public void startRunnerTrackerRoutine(int period) {
		TaskId = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(TheManHunt.getInstance(), () -> {

			for (UUID uuid : PlayerTracking.keySet()) {
				Player hunter = Bukkit.getPlayer(uuid);
				if (hunter == null) continue;
				Player runner = Bukkit.getPlayer(PlayerTracking.get(uuid));
				if (runner != null && runner.getWorld().equals(hunter.getWorld())) {

					if (hunter.getWorld().getEnvironment() == World.Environment.NORMAL) {
						ItemStack runnerTracker = null;
						for (ItemStack is : hunter.getInventory()) {
							if (RunnerTracker.getInstance().isRunnerTracker(is)) runnerTracker = is;
						}
						if (runnerTracker != null && ((CompassMeta) Objects.requireNonNull(runnerTracker.getItemMeta())).hasLodestone()) {
							runnerTracker.setItemMeta(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta());
						}
						hunter.setCompassTarget(runner.getLocation());

					} else {
						ItemStack runnerTracker = findRunnerTracker(hunter);
						if (runnerTracker != null) {
							CompassMeta cm = (CompassMeta) runnerTracker.getItemMeta();
							Location runnerLoc = runner.getLocation().getBlock().getLocation();
							assert cm != null;
							if (Settings.getInstance().AlwaysUpdateRunnerTracker) {
								if (runnerLoc.equals(cm.getLodestone())) {
									runnerLoc = runnerLoc.add(0, 10, 0);
								}
							}
							cm.setLodestone(runnerLoc);
							runnerTracker.setItemMeta(cm);
						}
					}
				}
			}

		}, 0, period).getTaskId();
	}

	public void stopRunnerTrackerRoutine() {
		if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
			Bukkit.getScheduler().cancelTask(TaskId);
		}
	}

	private ItemStack findRunnerTracker(Player player) {
		for (ItemStack is : player.getInventory()) {
			if (RunnerTracker.getInstance().isRunnerTracker(is)) {
				return is;
			}
		}
		return null;
	}

	@EventHandler
	public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(RunnerTracker.getInstance().getRunnerTrackerItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(final PlayerDeathEvent event) {
		if (event.getDrops().contains(RunnerTracker.getInstance().getRunnerTrackerItem())) {
			event.getDrops().remove(RunnerTracker.getInstance().getRunnerTrackerItem());
			returnTrackerToPlayer.add(event.getEntity().getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(final PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		UUID playerUUID = player.getUniqueId();
		if (returnTrackerToPlayer.contains(playerUUID)) {
			givePlayerRunnerTracker(player);
			returnTrackerToPlayer.remove(playerUUID);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.hasItem() &&
				(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
			assert event.getItem() != null;
			if (event.getItem().getType() != Material.COMPASS) return;
			if (event.getItem().getItemMeta() == null) return;
			if (!event.getItem().getItemMeta().getDisplayName().equals(RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta().getDisplayName())) return;
			if (event.getItem().getItemMeta().isUnbreakable() != RunnerTracker.getInstance().getRunnerTrackerItem().getItemMeta().isUnbreakable()) return;
			if (event.getClickedBlock() != null) {
				if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
					event.setCancelled(true);
					return;
				}
			}

			if (!RunnerTracker.getInstance().containsPlayerTrackingKey(player)) {
				RunnerTracker.getInstance().updatePlayerTracking(player, TeamHandler.getInstance().getTeam("Runners").getMember(0));
			}

			Player currentRunner = RunnerTracker.getInstance().getPlayerTrackingValue(player);
			int nextIndex = TeamHandler.getInstance().getTeam("Runners").getIndexOfMember(currentRunner) + 1;
			if (nextIndex >= TeamHandler.getInstance().getTeam("Runners").getMemberCount()) nextIndex = 0;
			Player runner = TeamHandler.getInstance().getTeam("Runners").getMember(nextIndex);
			RunnerTracker.getInstance().updatePlayerTracking(player, runner);
			LanguageManager.getInstance().sendMessage(player, Message.COMPASS_POINTS_TO, new String[] {runner.getDisplayName()});
		}
	}
}

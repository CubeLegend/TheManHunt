package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

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

			for (Player player : Bukkit.getOnlinePlayers()) {
				ItemStack compassItem = null;
				CompassMeta compassMeta = null;
				for (ItemStack is : player.getInventory().getContents()) {
					if (is == null) continue;
					if (is.getType() != Material.COMPASS) continue;
					if (is.getItemMeta() == null) continue;
					if (!is.getItemMeta().getDisplayName().equals(Objects.requireNonNull(this.getRunnerTrackerItem().getItemMeta()).getDisplayName())) continue;
					if (is.getItemMeta().isUnbreakable() == this.getRunnerTrackerItem().getItemMeta().isUnbreakable()) {
						compassItem = is;
						compassMeta = (CompassMeta) is.getItemMeta();
						break;
					}
				}
				if (compassItem != null) {
					if (!RunnerTracker.getInstance().containsPlayerTrackingKey(player)) {
						if (!CompassSpinning.getInstance().containsSpinPlayerCompass(player)) CompassSpinning.getInstance().addSpinPlayerCompass(player);
						continue;
					}
					Player runner = RunnerTracker.getInstance().getPlayerTrackingValue(player);
					if (runner == null) {
						if (!CompassSpinning.getInstance().containsSpinPlayerCompass(player)) CompassSpinning.getInstance().addSpinPlayerCompass(player);
						continue;
					}
					if (player.getWorld().getName().equals(runner.getWorld().getName())) {
						CompassSpinning.getInstance().removeSpinPlayerCompass(player);
						if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
							player.setCompassTarget(runner.getLocation());
						} else {
							compassMeta.setLodestone(runner.getLocation());
							compassItem.setItemMeta(compassMeta);
						}
						continue;
					}
					if (!CompassSpinning.getInstance().containsSpinPlayerCompass(player)) CompassSpinning.getInstance().addSpinPlayerCompass(player);
				}
			}
		}, 0, period).getTaskId();
	}

	public void stopRunnerTrackerRoutine() {
		if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
			Bukkit.getScheduler().cancelTask(TaskId);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(RunnerTracker.getInstance().getRunnerTrackerItem())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(final PlayerDeathEvent event) {
		event.getDrops().remove(RunnerTracker.getInstance().getRunnerTrackerItem());
	}

	@EventHandler
	public void onPlayerInteractEvent(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.hasItem() && Objects.equals(event.getItem(), RunnerTracker.getInstance().getRunnerTrackerItem()) &&
				(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
			if (event.getClickedBlock() != null) {
				if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
					event.setCancelled(true);
					return;
				}
			}

			if (!(TeamHandler.getInstance().getTeam("Runners").getMemberCount() > 0)) {
				if (!CompassSpinning.getInstance().containsSpinPlayerCompass(player)) CompassSpinning.getInstance().addSpinPlayerCompass(player);
				return;
			}

			if (!RunnerTracker.getInstance().containsPlayerTrackingKey(player)) {
				RunnerTracker.getInstance().updatePlayerTracking(player, TeamHandler.getInstance().getTeam("Runners").getMember(0));
			}

			Player currentRunner = RunnerTracker.getInstance().getPlayerTrackingValue(player);
			int nextIndex = TeamHandler.getInstance().getTeam("Runners").getIndexOfMember(currentRunner) + 1;
			if (nextIndex >= TeamHandler.getInstance().getTeam("Runners").getMemberCount()) nextIndex = 0;
			RunnerTracker.getInstance().updatePlayerTracking(player, TeamHandler.getInstance().getTeam("Runners").getMember(nextIndex));
			player.sendMessage(("§6Der Kompass zeigt jetzt auf §9" + TeamHandler.getInstance().getTeam("Runners").getMember(nextIndex).getDisplayName() + "§6."));
		}
	}
}

package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.CustomItem;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.Settings;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.List;
import java.util.Objects;

public class VillageTracker extends CustomItem {

    private static VillageTracker instance;

    public static VillageTracker getInstance() {
        if (instance == null) {
            instance = new VillageTracker();
        }
        return instance;
    }

    public VillageTracker() {
        super("Village Tracker", Material.COMPASS);
    }

    private int TaskId = 0;

    public void startVillageTrackingRoutine(int period) {
        TaskId = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(TheManHunt.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(this.getItem())) {
                    if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                        Location VillageLocation = player.getWorld().locateNearestStructure(
                                player.getLocation(),
                                StructureType.VILLAGE,
                                100,
                                false);
                        if (VillageLocation != null) {
                            if (Settings.getInstance().VillageTrackerUseLodestone) {
                                setLodestone(player, VillageLocation);
                            } else {
                                player.setCompassTarget(VillageLocation);
                            }
                        }
                    }
                }
            }
        }, 0, period).getTaskId();
    }

    public void stopVillageTrackingRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(TaskId)) {
            Bukkit.getScheduler().cancelTask(TaskId);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem()
                && Objects.requireNonNull(event.getItem()).getType() == Material.COMPASS
                && Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName().equals(Objects.requireNonNull(this.getItem().getItemMeta()).getDisplayName())
                && event.getItem().getItemMeta().isUnbreakable() == this.getItem().getItemMeta().isUnbreakable()
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {

            if (event.getClickedBlock() != null) {
                if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                LanguageManager.getInstance().sendMessage(player, Message.ERROR_USE_ONLY_IN_OVERWORLD, new String[0]);
            } else {
                Location villageLocation = player.getWorld().locateNearestStructure(
                        player.getLocation(),
                        StructureType.VILLAGE,
                        100,
                        false);
                assert villageLocation != null;
                villageLocation.setY(player.getLocation().getY());
                villageLocation.add(0.5, 0, 0.5);
                double villageDistance = player.getLocation().distance(villageLocation);
                int intVillageDistance = (int) Math.round(villageDistance);
                LanguageManager.getInstance().sendMessage(player, Message.NEXT_VILLAGE_X_BLOCKS_AWAY, new String[] {String.valueOf(intVillageDistance)});
            }
        }
    }

    void setLodestone(Player player, Location target) {
        ItemStack villageTracker = null;
        for (ItemStack is : player.getInventory()) {
            if (this.getItem().equals(is)) {
                villageTracker =  is;
                break;
            }
        }
        if (villageTracker != null) {
            CompassMeta cm = (CompassMeta) villageTracker.getItemMeta();
            assert cm != null;
            cm.setLodestone(target);
            villageTracker.setItemMeta(cm);
        }
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() != GameState.IDLE) return;
        if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
            if (Settings.getInstance().VillageTracker) {
                List<Player> runners = TeamHandler.getInstance().getTeam("Runners").getMembers();
                for (Player runner : runners) {
                    VillageTracker.getInstance().giveToPlayer(runner);
                }
            }
        }
    }
}

package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.NMSUtils.MinecraftStructures;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VillageTracker {

    private static VillageTracker instance;

    public static VillageTracker getInstance() {
        if (instance == null) {
            instance = new VillageTracker();
        }
        return instance;
    }

    public ItemStack getVillageTrackerItem() {
        ItemStack compass = new ItemStack(Material.COMPASS, 1);

        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("Village Tracker");
        meta.setUnbreakable(true);
        compass.setItemMeta(meta);

        return compass;
    }

    public void givePlayerVillageTracker(Player player) {
        ItemStack compass = getVillageTrackerItem();

        player.getInventory().addItem(compass);
    }

    private int TaskId = 0;

    public void startVillageTracking(int period) {
        TaskId = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(TheManHunt.getInstance(), (Runnable) () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(VillageTracker.getInstance().getVillageTrackerItem())) {
                    if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                        if (CompassSpinning.getInstance().containsSpinPlayerCompass(player)) {
                            CompassSpinning.getInstance().removeSpinPlayerCompass(player);
                        }
                        Location VillageLocation = MinecraftStructures.getStructureLocation(player.getWorld(), player.getLocation(), "Village");
                        if (VillageLocation != null) {
                            player.setCompassTarget(VillageLocation);
                        }
                    }
                    if (!CompassSpinning.getInstance().containsSpinPlayerCompass(player)) {
                        CompassSpinning.getInstance().addSpinPlayerCompass(player);
                    }
                }
            }
        }, 0, period).getTaskId();
    }

    public void stopVillageTracking() {
        Bukkit.getScheduler().cancelTask(TaskId);
    }
}

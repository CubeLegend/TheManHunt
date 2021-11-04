package me.CubeLegend.TheManHunt.Compass;

import me.CubeLegend.TheManHunt.NMSUtils.MinecraftStructures;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class VillageTracker implements Listener {

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
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        compass.setItemMeta(meta);

        return compass;
    }

    public void givePlayerVillageTracker(Player player) {
        ItemStack compass = getVillageTrackerItem();

        player.getInventory().addItem(compass);
    }

    private int TaskId = 0;

    public void startVillageTrackingRoutine(int period) {
        TaskId = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(TheManHunt.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(this.getVillageTrackerItem())) {
                    if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                        Location VillageLocation = MinecraftStructures.getStructureLocation(player.getLocation(), "Village");
                        if (VillageLocation != null) {
                            player.setCompassTarget(VillageLocation);
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
    public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.COMPASS
                && Objects.requireNonNull(event.getItemDrop().getItemStack().getItemMeta()).getDisplayName().equals(Objects.requireNonNull(this.getVillageTrackerItem().getItemMeta()).getDisplayName())
                && event.getItemDrop().getItemStack().getItemMeta().isUnbreakable() == this.getVillageTrackerItem().getItemMeta().isUnbreakable()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {
        event.getDrops().remove(VillageTracker.getInstance().getVillageTrackerItem());
    }

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem()
                && Objects.requireNonNull(event.getItem()).getType() == Material.COMPASS
                && Objects.requireNonNull(event.getItem().getItemMeta()).getDisplayName().equals(Objects.requireNonNull(this.getVillageTrackerItem().getItemMeta()).getDisplayName())
                && event.getItem().getItemMeta().isUnbreakable() == this.getVillageTrackerItem().getItemMeta().isUnbreakable()
                && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {

            if (event.getClickedBlock() != null) {
                if (Objects.requireNonNull(event.getClickedBlock()).getType() == Material.LODESTONE) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                player.sendMessage(("§cDu kannst den Village Tracker nur in der Overworld benutzen!"));
            } else {
                Location villageLocation = MinecraftStructures.getStructureLocation(player.getLocation(), "Village");
                assert villageLocation != null;
                villageLocation.setY(player.getLocation().getY());
                villageLocation.add(0.5, 0, 0.5);
                double villageDistance = player.getLocation().distance(villageLocation);
                int intVillageDistance = (int) Math.round(villageDistance);
                player.sendMessage(("§6Das nähste Dorf ist §b" + intVillageDistance + " §6Blöcke entfernt."));
            }
        }
    }
}
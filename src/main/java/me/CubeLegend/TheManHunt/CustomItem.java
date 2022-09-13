package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
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

import java.util.ArrayList;
import java.util.UUID;

public class CustomItem implements Listener {

    private static CustomItem instance;

    public CustomItem(String name, Material material) {
        this.name = name;
        this.material = material;
    }

    private final String name;
    private final Material material;

    private final ArrayList<UUID> returnItem = new ArrayList<>();

    public ItemStack getItem() {
        ItemStack compass = new ItemStack(material, 1);

        ItemMeta meta = compass.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        compass.setItemMeta(meta);
        return compass;
    }

    public void giveToPlayer(Player player) {
        ItemStack compass = getItem();
        player.getInventory().addItem(compass);
    }

    public void removeFromPlayer(Player player) {
        ItemStack compass = getItem();
        player.getInventory().remove(compass);
    }

    @EventHandler
    public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(this.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(final PlayerDeathEvent event) {
        if (event.getDrops().contains(this.getItem())) {
            event.getDrops().remove(this.getItem());
            returnItem.add(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (returnItem.contains(playerUUID)) {
            this.giveToPlayer(player);
            returnItem.remove(playerUUID);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        if (event.hasItem() &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            assert event.getItem() != null;
            if (!event.getItem().equals(this.getItem())) return;

            Bukkit.getLogger().warning("The CustomItem class should not be used by itself!");
        }
    }

}

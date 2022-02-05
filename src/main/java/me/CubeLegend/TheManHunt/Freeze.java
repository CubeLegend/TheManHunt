package me.CubeLegend.TheManHunt;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Freeze implements Listener {

    private static Freeze instance;

    public static Freeze getInstance() {
        if (instance == null) {
            instance = new Freeze();
        }
        return instance;
    }

    private final ArrayList<UUID> frozenPlayers = new ArrayList<>();

    public void addFrozenPlayers(List<Player> players) {
        for (Player player : players) {
            UUID uuid = player.getUniqueId();
            if (!frozenPlayers.contains(uuid)) {
                frozenPlayers.add(uuid);
            }
        }
    }

    public void removeFrozenPlayers(List<Player> players) {
        for (Player player : players) {
            UUID uuid = player.getUniqueId();
            frozenPlayers.remove(uuid);
        }
    }

    public ArrayList<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    private final ArrayList<UUID> frozenLivingEntities = new ArrayList<>();

    public void addFrozenEntities(List<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            UUID uuid = entity.getUniqueId();
            if (!frozenLivingEntities.contains(uuid)) {
                frozenLivingEntities.add(uuid);
            }
        }
    }

    public void removeFrozenEntities(List<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            UUID uuid = entity.getUniqueId();
            frozenLivingEntities.remove(uuid);
        }
    }

    //Freeze Events ----------------------
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // if the attacker is frozen the event gets canceled
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        if (frozenPlayers.contains(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // if the player that gets attacked is attacked by somebody that froze him the event gets canceled
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
//        if (frozenPlayers.contains(player.getUniqueId()) && vision.contains(damager.getUniqueId())) {
//            event.setCancelled(true);
//        }
    }
    //------------------------------------
}

package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.StateSystem.GameStateChangeEvent;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
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

    private final Configuration config = Configuration.getInstance();

    public void addFrozenPlayers(List<UUID> players) {
        for (UUID uuidP : players) {
            if (!frozenPlayers.contains(uuidP)) {
                frozenPlayers.add(uuidP);
            }
        }
    }

    public void removeFrozenPlayers(List<UUID> players) {
        for (UUID uuidP : players) {
            frozenPlayers.remove(uuidP);
        }
    }

    public ArrayList<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    private final ArrayList<UUID> frozenLivingEntities = new ArrayList<>();

    public void addFrozenEntities(List<UUID> entities) {
        for (UUID uuidE : entities) {
            if (!frozenLivingEntities.contains(uuidE)) {
                frozenLivingEntities.add(uuidE);
            }
        }
    }

    public void removeFrozenEntities(List<UUID> entities) {
        for (UUID uuidE : entities) {
            frozenLivingEntities.remove(uuidE);
        }
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event) {
        if (event.getChangeFrom() == GameState.IDLE) {
            if (event.getChangeTo() == GameState.RUNAWAYTIME) {
                this.addFrozenPlayers(TeamHandler.getInstance().getTeam("Hunters").getMembersRaw());
            }
            if (event.getChangeTo() == GameState.RUNAWAYTIME || event.getChangeTo() == GameState.PLAYING) {
                if (config.getBoolean("Abilities.Runner.FreezeVision")) {
                    for (UUID runner : TeamHandler.getInstance().getTeam("Runners").getMembersRaw()) {
                        FreezeVision.getInstance().givePlayerFreezeVision(runner);
                    }
                }
            }
        }
        else if (event.getChangeFrom() == GameState.PLAYING) {
            if (event.getChangeTo() == GameState.END) {
                if (config.getBoolean("Abilities.Runner.FreezeVision")) {
                    for (UUID runner : TeamHandler.getInstance().getTeam("Runners").getMembersRaw()) {
                        FreezeVision.getInstance().takePlayerFreezeVision(runner);
                    }
                }
            }
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
        if (!(event.getDamager() instanceof Player damager)) return;
        if (frozenPlayers.contains(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // if the player that is attacked by somebody that froze him the event gets canceled
        if (!(event.getEntity() instanceof Player player)) return;
        if (!frozenPlayers.contains(player.getUniqueId())) return;
        if (FreezeVision.getInstance().getPlayersWithFreezeVision().contains(damager)) {
            event.setCancelled(true);
        }
    }
    //------------------------------------
}

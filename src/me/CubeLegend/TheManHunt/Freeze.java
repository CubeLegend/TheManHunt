package me.CubeLegend.TheManHunt;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;
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
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public class Freeze implements Listener {

    private static Freeze instance;

    public static Freeze getInstance() {
        if (instance == null) {
            instance = new Freeze();
        }
        return instance;
    }

    private final ArrayList<UUID> vision = new ArrayList<>();

    private final ArrayList<UUID> frozenPlayers = new ArrayList<>();

    private final ArrayList<UUID> frozenEntities = new ArrayList<>();

    private int FreezeVisionRoutine;

    public void addPlayersToVision(String teamName) {
        for (Player player : TeamHandler.getInstance().getTeam(teamName).getMembers()) {
            if (!vision.contains(player.getUniqueId())) vision.add(player.getUniqueId());
        }
    }

    public void removePlayerFromVision(Player player) {
        vision.remove(player.getUniqueId());
    }

    public void startFreezeVisionRoutine(long period) {
        FreezeVisionRoutine = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TheManHunt.getInstance(), () -> {
            frozenPlayers.clear();
            for (UUID freezedEntity : frozenEntities) {
                ((LivingEntity) Objects.requireNonNull(Bukkit.getEntity(freezedEntity))).setAI(true);
            }
            frozenEntities.clear();

            for (UUID uuid : vision) {
                for (LivingEntity entity : entitiesInLineOfSight(Bukkit.getPlayer(uuid))) {
                    if (entity instanceof Player) {
                        if (!frozenPlayers.contains(entity.getUniqueId())) {
                            frozenPlayers.add(entity.getUniqueId());
                        }
                    } else {
                        if (!frozenEntities.contains(entity.getUniqueId())) {
                            frozenEntities.add(entity.getUniqueId());
                            entity.setAI(false);
                        }
                    }
                }
            }
        }, 0, period);
    }

    public void stopFreezeVisionRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(FreezeVisionRoutine)) {
            Bukkit.getScheduler().cancelTask(FreezeVisionRoutine);
        }
    }

    public List<LivingEntity> entitiesInLineOfSight(Player player) {
        if (player == null) return null;
        Location playerLoc = player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0));
        List<LivingEntity> entities = new ArrayList<>();
        double furthestEntitydis = 0;
        final double EntityPosYOffset = 1;
        final double circleHitboxSize = 1;
        int range = 100;

        for (Entity other : player.getNearbyEntities(range, range, range)) {
            if (!(other instanceof LivingEntity)) continue;
            Location otherLoc = other.getLocation().add(new Location(other.getWorld(), 0, EntityPosYOffset, 0));
            final Vector difference = otherLoc.toVector().subtract(playerLoc.toVector()); // Vectors mutate very easily. You should always use .clone(), if you use vector.dot(), vector.crossproduct(), etc

            if (difference.clone().normalize().dot(playerLoc.getDirection()) > 0) { // Check if the Player is looking towards or away from other with the dot product
                if (playerLoc.getDirection().crossProduct(difference).lengthSquared() < circleHitboxSize) { // Check if the Player is looking at other or the reverse position
                    double distance = playerLoc.distance(otherLoc);
                    if(isBlockInWay(player, distance)) {
                        entities.add((LivingEntity) other);
                        if (playerLoc.distance(otherLoc) > furthestEntitydis) {
                            furthestEntitydis = playerLoc.distance(otherLoc);
                        }
                    }
                }
            }
        }
        drawLine(playerLoc, playerLoc.getDirection().multiply(furthestEntitydis).add(playerLoc.toVector()).toLocation(player.getWorld()), 0.25f, 0.25f, Color.RED);
        return entities;
    }

    public static boolean isBlockInWay(Player player, double distance) {
        Vector v1 = player.getEyeLocation().toVector(); //start point
        Vector v2 = player.getLocation().getDirection(); // direction
        int distance1 = (int) distance;

        final BlockIterator bItr = new BlockIterator(player.getWorld(), v1, v2, 0.0D, distance1);
        while (bItr.hasNext()) {
            final Block block = bItr.next();
            if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR && block.getType() != Material.WATER) {
                return false;
            }
        }
        return true;
    }

    public static void drawLine(
            Location point1, // Start point
            Location point2, // End pont
            double space, // Space between each particle
            float size, // Size of dust particles
            Color color // Color of dust
            //Particle.DustOptions dust // Set the color and size of your dust with "new Particle.DustOptions(Color.fromRGB(255, 0, 0), 0.25f)"
    ) {

        World world = point1.getWorld();

        /*Throw an error if the points are in different worlds*/
        Validate.isTrue(Objects.equals(point2.getWorld(), world), "Lines cannot be in different worlds!");

        /*Distance between the two particles*/
        double distance = point1.distance(point2);

        /* The points as vectors */
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();

        /* Subtract gives you a vector between the points, we multiply by the space*/
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);

        /*The distance covered*/
        double covered = 0;

        Particle.DustOptions dust = new Particle.DustOptions(color, size);
        /* We run this code while we haven't covered the distance, we increase the point by the space every time*/
        for (; covered < distance; p1.add(vector)) {
            if (p1.distance(point1.toVector()) > 0.3 && p1.distance(p2) > 0.3) {
                /*Spawn the particle at the point*/
                assert world != null;
                world.spawnParticle(Particle.REDSTONE, p1.getX(), p1.getY(), p1.getZ(), 0, 0.0, 0.0, 0.0, (Object)dust);
            }
            /* We add the space covered */
            covered += space;
        }
    }

    //Freeze Events ----------------------
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        if (frozenPlayers.contains(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (frozenPlayers.contains(player.getUniqueId()) && vision.contains(damager.getUniqueId())) {
            event.setCancelled(true);
        }
    }
    //------------------------------------

    /* Would be inserted in the entities in line of sight for loop. This shows vectors that are useful for raycasting.
    //draw difference of entity location and player location in yellow
    drawLine(new Location(player.getWorld(), 0, 100, 0), difference.clone().toLocation(player.getWorld()).add(new Vector(0, 100, 0)), 0.2, 0.25f, Color.YELLOW);
    //draw normalized player direction in red
    drawLine(new Location(player.getWorld(), 0, 100, 0), playerLoc.getDirection().add(new Vector(0, 100, 0)).toLocation(player.getWorld()), 0.2, 0.25f, Color.RED);
    //draw crossProduct of player direction and difference in green
    drawLine(new Location(player.getWorld(), 0, 100, 0), playerLoc.getDirection().crossProduct(difference).add(new Vector(0, 100, 0)).toLocation(player.getWorld()), 0.2, 0.25f, Color.GREEN);
     */

}

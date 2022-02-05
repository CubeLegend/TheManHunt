package me.CubeLegend.TheManHunt.SpecialAbilities;

import me.CubeLegend.TheManHunt.Freeze;
import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TheManHunt;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FreezeVision {

    private static FreezeVision instance;

    public static FreezeVision getInstance() {
        if (instance == null) {
            instance = new FreezeVision();
        }
        return instance;
    }

    private final ArrayList<UUID> freezeVision = new ArrayList<>();

    private final List<Player> playersILOS = new ArrayList<>(); // ILOS = in line of sight
    private final List<Player> recentPlayersILOS = new ArrayList<>(); // ILOS = in line of sight
    private final List<LivingEntity> livingEntitiesILOS = new ArrayList<>();
    private final List<LivingEntity> recentLivingEntitiesILOS = new ArrayList<>();

    private int freezeVisionRoutine;

    public void givePlayerFreezeVision(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        if (!freezeVision.contains(uuid)) {
            freezeVision.add(uuid);
        }
    }

    public void takePlayerFreezeVision(@NotNull Player player) {
        freezeVision.remove(player.getUniqueId());
    }

    public void startFreezeVisionRoutine(long period) {
        freezeVisionRoutine = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TheManHunt.getInstance(), () -> {
            if (GameHandler.getInstance().getGameState() != GameState.PLAYING) { return; }
            for (UUID uuid : freezeVision) {
                for (LivingEntity livingEntity : entitiesInLineOfSight(Bukkit.getPlayer(uuid))) {
                    if (livingEntity instanceof Player player) {
                        if (!playersILOS.contains(player)) {
                            playersILOS.add(player);
                        }
                    } else {
                        if (!livingEntitiesILOS.contains(livingEntity)) {
                            livingEntitiesILOS.add(livingEntity);
                        }
                    }
                }
            }
            Freeze.getInstance().addFrozenPlayers(playersILOS);
            Freeze.getInstance().addFrozenEntities(livingEntitiesILOS);
            removeRecentPlayersFromFreeze();
            removeRecentEntitiesFromFreeze();
        }, 0, period);
    }

    public void stopFreezeVisionRoutine() {
        if (Bukkit.getScheduler().isCurrentlyRunning(freezeVisionRoutine)) {
            Bukkit.getScheduler().cancelTask(freezeVisionRoutine);
        }
    }

    private void removeRecentPlayersFromFreeze() {
        ArrayList<Player> toRemove = new ArrayList<>();
        for (Player player : recentPlayersILOS) {
            if (playersILOS.contains(player)) continue;
            toRemove.add(player);
        }
        Freeze.getInstance().removeFrozenPlayers(toRemove);
        recentPlayersILOS.addAll(playersILOS);
        playersILOS.clear();
    }

    private void removeRecentEntitiesFromFreeze() {
        ArrayList<LivingEntity> toRemove = new ArrayList<>();
        for (LivingEntity livingEntity : recentLivingEntitiesILOS) {
            if (livingEntitiesILOS.contains(livingEntity)) continue;
            toRemove.add(livingEntity);
        }
        Freeze.getInstance().removeFrozenEntities(toRemove);
        recentLivingEntitiesILOS.addAll(livingEntitiesILOS);
        livingEntitiesILOS.clear();
    }

    public List<LivingEntity> entitiesInLineOfSight(Player player) {
        if (player == null) return null;
        Location playerLoc = player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0));
        List<LivingEntity> entities = new ArrayList<>();
        double furthestEntityDis = 0;
        final double EntityPosYOffset = 1;
        final double circleHitBoxSize = 1;
        int range = 100;

        for (Entity other : player.getNearbyEntities(range, range, range)) {
            if (!(other instanceof LivingEntity)) continue;
            Location otherLoc = other.getLocation().add(new Location(other.getWorld(), 0, EntityPosYOffset, 0));
            final Vector difference = otherLoc.toVector().subtract(playerLoc.toVector()); // Vectors mutate very easily. You should always use .clone(), if you use vector.dot(), vector.crossproduct(), etc

            if (difference.clone().normalize().dot(playerLoc.getDirection()) > 0) { // Check if the Player is looking towards or away from other with the dot product
                if (playerLoc.getDirection().crossProduct(difference).lengthSquared() < circleHitBoxSize) { // Check if the Player is looking at other or the reverse position
                    double distance = playerLoc.distance(otherLoc);
                    if(isBlockInWay(player, distance)) {
                        entities.add((LivingEntity) other);
                        if (playerLoc.distance(otherLoc) > furthestEntityDis) {
                            furthestEntityDis = playerLoc.distance(otherLoc);
                        }
                    }
                }
            }
        }
        drawLine(playerLoc, playerLoc.getDirection().multiply(furthestEntityDis).add(playerLoc.toVector()).toLocation(player.getWorld()), 0.25f, 0.25f, Color.RED);
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

        /*Distance between the two points*/
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
}

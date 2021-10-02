package me.CubeLegend.TheManHunt;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RayCast {

    /*
    public void getVectors(Player player) {
        Vector playerLoc = player.getLocation().toVector();
        player.sendMessage("X: " + playerLoc.getX());
        player.sendMessage("Y: " + playerLoc.getY());
        player.sendMessage("Z: " + playerLoc.getZ());

        System.out.println("X: " + playerLoc.getX());
        System.out.println("Y: " + playerLoc.getY());
        System.out.println("Z: " + playerLoc.getZ());

        //if (player.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(player.getLocation().getDirection().normalize()) >= 0)
    }

    public void getCrossProduct(Player player, Entity entity) {
        message(player, "----------------------------------");
        Vector playerLoc = player.getLocation().getDirection().normalize();
        message(player, "X: " + playerLoc.getX());
        message(player, "Y: " + playerLoc.getY());
        message(player, "Z: " + playerLoc.getZ());
        message(player, "----------------------------------");

        Vector entityLoc = entity.getLocation().toVector().normalize();
        message(player, "X: " + entityLoc.getX());
        message(player, "Y: " + entityLoc.getY());
        message(player, "Z: " + entityLoc.getZ());
        message(player, "----------------------------------");

        Vector crossProduct = playerLoc.getCrossProduct(entityLoc);
        message(player, "X: " + crossProduct.getX());
        message(player, "Y: " + crossProduct.getY());
        message(player, "Z: " + crossProduct.getZ());
        message(player, "----------------------------------");
    }

    public void getDotProduct(Player player, Entity entity) {
        message(player, "----------------------------------");
        Vector playerLocDir = player.getLocation().getDirection().normalize();
        message(player, "X: " + playerLocDir.getX());
        message(player, "Y: " + playerLocDir.getY());
        message(player, "Z: " + playerLocDir.getZ());
        message(player, "----------------------------------");

        Vector entityLoc = entity.getLocation().toVector().normalize();
        message(player, "X: " + entityLoc.getX());
        message(player, "Y: " + entityLoc.getY());
        message(player, "Z: " + entityLoc.getZ());
        message(player, "----------------------------------");

        double dotProduct = entityLoc.dot(playerLocDir);
        message(player, "Dot Product: " + dotProduct);
        message(player, "----------------------------------");
    }
    */

    public List<Entity> entitiesInLineOfSight(Player player) {
        if (player == null) return null;
        Location playerLoc = player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0));
        List<Entity> entities = new ArrayList<>();
        final double EntityPosYOffset = 1;
        final double circleHitboxSize = 1;
        int range = 100;

        for (Entity other : player.getNearbyEntities(range, range, range)) {
            Location otherLoc = other.getLocation().add(new Location(other.getWorld(), 0, EntityPosYOffset, 0));
            final Vector difference = otherLoc.toVector().subtract(playerLoc.toVector()); // Vectors mutate very easily. You should always use .clone(), if you use vector.dot(), vector.crossproduct(), etc

            if (difference.clone().normalize().dot(playerLoc.getDirection()) > 0) { // Check if the Player is looking towards or away from other with the dot product
                if (playerLoc.getDirection().crossProduct(difference).lengthSquared() < circleHitboxSize) { // Check if the Player is looking at other or the reverse position
                    double distance = playerLoc.distance(otherLoc);
                    if(isBlockInWay(player, distance)) {
                        System.out.println("Test 3");
                        entities.add(other);
                    }
                }
            }
        }
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

    /*public Entity getTarget(Player player) {
        if (player == null)
            return null;
        Entity target = null;
        final double threshold = 1;
        for (Entity other : player.getWorld().getEntities()) {
            if (other.getWorld() == player.getWorld()) {
                final Vector difference = other.getLocation().toVector().subtract(player.getLocation().toVector());

                //draw difference of entity location and player location in yellow
                drawLine(new Location(player.getWorld(), 0, 100, 0), difference.clone().toLocation(player.getWorld()).add(new Vector(0, 100, 0)), 0.2, 0.25f, Color.YELLOW);
                //draw normalized player direction in red
                drawLine(new Location(player.getWorld(), 0, 100, 0), playerLoc.getDirection().add(new Vector(0, 100, 0)).toLocation(player.getWorld()), 0.2, 0.25f, Color.RED);
                //draw crossProduct of player direction and difference in green
                drawLine(new Location(player.getWorld(), 0, 100, 0), playerLoc.getDirection().crossProduct(difference).add(new Vector(0, 100, 0)).toLocation(player.getWorld()), 0.2, 0.25f, Color.GREEN);

                if (player.getLocation().getDirection().normalize().crossProduct(difference).lengthSquared() < threshold && difference.normalize().dot(player.getLocation().getDirection().normalize()) >= 0) {

                    double distance = player.getLocation().distance(other.getLocation());
                    if(isBlockInWay(player, distance)) {
                        if (target == null || target.getLocation().distanceSquared(player.getLocation()) > other.getLocation().distanceSquared(player.getLocation()))
                            target = other;
                    }
                }
            }
        }
        return target;
    }*/

    public void showLineToTarget(Player player, Entity target) {
        Vector PlayerLoc = player.getLocation().toVector().add(new Vector(0, player.getEyeHeight(), 0));
        Vector TargetLoc = target.getLocation().toVector().add(new Vector(0, 1.5, 0));
        Vector VecToTarget;

        VecToTarget = TargetLoc.subtract(PlayerLoc).add(PlayerLoc);
        drawLine(VecToTarget.toLocation(target.getWorld()), PlayerLoc.toLocation(player.getWorld()), 0.25f, 0.25f, Color.RED);
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
}

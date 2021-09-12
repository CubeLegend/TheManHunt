package me.CubeLegend.TheManHunt;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class RayCast {

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
}

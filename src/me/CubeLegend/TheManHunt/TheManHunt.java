package me.CubeLegend.TheManHunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class TheManHunt extends JavaPlugin {

    private static TheManHunt instance;

    public static TheManHunt getInstance() {
        return instance;
    }

    @Override
    public void onEnable(){
        instance = this;

        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        registerListeners();
        registerPluginMessageingChannels();
        config.options().copyDefaults(true);
        this.saveConfig();

        Freeze.getInstance().startFreezeVisionRoutine(1);
    }

    @Override
    public void onDisable(){
        unregisterPluginMessageingChannels();
        Freeze.getInstance().stopFreezeVisionRoutine();
    }

    private int id1 = 0;
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("membersof")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Team team = TeamHandler.getInstance().getTeam(args[0]);
                if (team != null) {
                    player.sendMessage("§6Members of Team " + team.getTeamName() + ":");
                    for (Player current : Bukkit.getOnlinePlayers()) {
                        if (TeamHandler.getInstance().getTeam(args[0]).checkForMember(current)) {
                            player.sendMessage(current.getDisplayName());
                        }
                    }
                    return true;
                }
            }
        }

        if (label.equalsIgnoreCase("vectorof")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                RayCast rc = new RayCast();
                //rc.getVectors(player);
                if (Bukkit.getScheduler().isCurrentlyRunning(id1)) {
                    Bukkit.getScheduler().cancelTask(id1);
                    return false;
                }
                StringBuilder msg = new StringBuilder("Entites in line of sight: ");
                for (Entity entity : rc.entitiesInLineOfSight(player)) {
                    msg.append(entity.getType()).append(", ");
                }
                player.sendMessage(msg.toString());

                /*
                id1 = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(TheManHunt.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                rc.entitiesInLineOfSight(player);
                            }
                        }, 0, 1);
                */

                return true;
            }
        }
        if (label.equalsIgnoreCase("crossproduct")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                RayCast rc = new RayCast();
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                //rc.getCrossProduct(player, nearestEntity);
                System.out.println(player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0)));
                System.out.println(nearestEntity.getLocation().add(new Vector(0, 1, 0)).toVector().subtract(player.getLocation().add(new Vector(0, player.getEyeHeight(), 0)).toVector()));
                player.sendMessage(player.getLocation().add(new Location(player.getWorld(), 0, player.getEyeHeight(), 0)).getDirection().crossProduct(nearestEntity.getLocation().add(new Vector(0, 1, 0)).toVector().subtract(player.getLocation().add(new Vector(0, player.getEyeHeight(), 0)).toVector())).lengthSquared() + " < threshold: 1");
                return true;
            }
        }
        if (label.equalsIgnoreCase("dotproduct")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                RayCast rc = new RayCast();
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                //rc.getDotProduct(player, nearestEntity);
                player.sendMessage(String.valueOf(nearestEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().dot(player.getLocation().getDirection().normalize())) + " >= 0");
                return true;
            }
        }
        if (label.equalsIgnoreCase("showLine")) {
            Freeze.getInstance().addPlayersToVision("Runners");

            /*
            if (sender instanceof Player) {
                Player player = (Player) sender;
                RayCast rc = new RayCast();
                Entity nearestEntity = null;
                for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                    if (nearestEntity == null) {
                        nearestEntity = entity;
                    } else if (entity.getLocation().distance(player.getLocation()) < nearestEntity.getLocation().distance(player.getLocation())) {
                        nearestEntity = entity;
                    }
                }
                assert nearestEntity != null;
                rc.showLineToTarget(player, nearestEntity);
                return true;
            }
             */
        }
        return false;
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

    }

    private void registerPluginMessageingChannels() {
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")), new MessageListener());
    }

    private void unregisterPluginMessageingChannels() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")));
    }
}

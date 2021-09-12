package me.CubeLegend.TheManHunt;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

    @Override
    public void onDisable(){
        unregisterPluginMessageingChannels();
    }

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
                rc.getVectors(player);
                return true;
            }
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

package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandJoin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("join")) {
            if (args.length > 2) {
                return true;
            }
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running§r");
                return true;
            }
            if (args.length == 1 && (args[0].equalsIgnoreCase("Runners") || args[0].equalsIgnoreCase("Hunters"))) {
                if (sender instanceof Player) {
                    TeamHandler.getInstance().getTeam(args[0]).addMember((Player) sender);
                }
            }
            if (args.length == 2) {
                if (Bukkit.getPlayer(args[0]) == null) {
                    return true;
                }
                if (!(args[1].equalsIgnoreCase("Runners") || args[1].equalsIgnoreCase("Hunters"))) {
                    return true;
                }
                TeamHandler.getInstance().getTeam(args[1]).addMember(Objects.requireNonNull(Bukkit.getPlayer(args[0])));
            }
        }

        // the command was used correctly
        return true;
    }
}

package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.Team;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandJoin implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (args.length > 2 || args.length == 0) return false;
        if (sender instanceof Player) {
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running");
                return true;
            }
            if (args.length == 1) {
                if (!args[0].equals("Runners") && !args[0].equals("Hunters")) {
                    sender.sendMessage("§cA team with the name " + args[0] + " does not exist");
                    return true;
                }
                Team team = TeamHandler.getInstance().getTeam(args[0]);
                team.addMember((Player) sender);
                sender.sendMessage("§6You got added to team " + team.getTeamColor() +  team.getTeamName());
                return true;
            } else {
                if (Bukkit.getPlayer(args[0]) == null) {
                    sender.sendMessage("§c" + args[0] + " is not online");
                    return true;
                }
                if (!args[1].equals("Runners") && !args[1].equals("Hunters")) {
                    sender.sendMessage("§cA team with the name " + args[0] + " does not exist");
                    return true;
                }
                Team team = TeamHandler.getInstance().getTeam(args[1]);
                Player player = Bukkit.getPlayer(args[0]);
                assert player != null;
                team.addMember(player);
                sender.sendMessage(player.getDisplayName() + " §6got added to team " + team.getTeamColor() + team.getTeamName());
                return true;
            }
        } else {
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage("§cYou need to be a Player in order to be added to a team");
                return true;
            } else {
                if (Bukkit.getPlayer(args[0]) == null) {
                    sender.sendMessage("§c" + args[0] + " is not online");
                    return true;
                }
                if (!args[1].equals("Runners") && !args[1].equals("Hunters")) {
                    sender.sendMessage("§cA team with the name " + args[0] + " does not exist");
                    return true;
                }
                Team team = TeamHandler.getInstance().getTeam(args[1]);
                Player player = Bukkit.getPlayer(args[0]);
                assert player != null;
                team.addMember(player);
                sender.sendMessage(player.getDisplayName() + " §6got added to team " + team.getTeamColor() + team.getTeamName());
                return true;
            }
        }
    }
}

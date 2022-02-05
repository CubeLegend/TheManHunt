package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (args.length != 0) return true;
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running§r");
                return false;
            }
            if (TeamHandler.getInstance().getTeam("Runners").getMemberCount() < 1 ||
                    TeamHandler.getInstance().getTeam("Hunters").getMemberCount() < 1) {
                sender.sendMessage("There needs to be at least one Runner and one Hunter");
                return false;
            }
            GameHandler.getInstance().setGameState(GameState.RUNAWAYTIME);
            return false;
        }
        // the command was used incorrectly
        return true;
    }
}

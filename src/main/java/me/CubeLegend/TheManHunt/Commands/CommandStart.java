package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {

            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running");
                return true;
            }
            if (TeamHandler.getInstance().getTeam("Runners").getMemberCount() < 1 ||
                    TeamHandler.getInstance().getTeam("Hunters").getMemberCount() < 1) {
                sender.sendMessage("There needs to be at least one Runner and one Hunter");
                return true;
            }
            GameHandler.getInstance().setGameState(GameState.RUNAWAYTIME);
            return true;
        }
        // the command was used incorrectly
        return false;
    }
}

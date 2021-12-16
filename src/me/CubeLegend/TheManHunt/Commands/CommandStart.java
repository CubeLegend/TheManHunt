package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("start")) {
            if (args != null) return true;
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running§r");
                return true;
            }
            GameHandler.getInstance().setGameState(GameState.RUNAWAYTIME);
        }
        // the command was used correctly
        return true;
    }
}

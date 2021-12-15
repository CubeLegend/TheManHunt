package me.CubeLegend.TheManHunt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("start")) {
            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                sender.sendMessage("§cThe game is already running§r");
                return true;
            }
            HunterWaitTimer.getInstance().startTimer();
        }

        // the command was used correctly
        return true;
    }
}

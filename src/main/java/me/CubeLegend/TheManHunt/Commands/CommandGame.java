package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.Freeze;
import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.HunterWaitTimer;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CommandGame implements TabExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!commandSender.hasPermission("TheManHunt.GameManagement.CmdGame")) return true;
        if (args.length != 1) return false;
        if (args[0].equals("start")) {

            if (GameHandler.getInstance().getGameState() != GameState.IDLE) {
                LanguageManager.getInstance().sendMessage(commandSender, Message.ERROR_GAME_IS_RUNNING, new String[0]);
                return true;
            }
            if (TeamHandler.getInstance().getTeam("Runners").getMemberCount() < 1 ||
                    TeamHandler.getInstance().getTeam("Hunters").getMemberCount() < 1) {
                LanguageManager.getInstance().sendMessage(commandSender, Message.ERROR_NOT_ENOUGH_TEAM_MEMBERS, new String[0]);
                return true;
            }
            GameHandler.getInstance().setGameState(GameState.RUNAWAYTIME);
            return true;
        }
        if (args[0].equals("stop")) {
            if (!(GameHandler.getInstance().getGameState() == GameState.PLAYING || GameHandler.getInstance().getGameState() == GameState.RUNAWAYTIME)) {
                LanguageManager.getInstance().sendMessage(commandSender, Message.ERROR_GAME_IS_NOT_RUNNING, new String[0]);
                return true;
            }
            LanguageManager.getInstance().broadcastMessage(Message.GAME_STOPPED, new String[0]);
            HunterWaitTimer.getInstance().stopTimer();
            Freeze.getInstance().removeFrozenPlayers(TeamHandler.getInstance().getTeam("Hunters").getMembers());
            GameHandler.getInstance().setGameState(GameState.END);
            return true;
        }
        return false;
    }

    List<String> arguments = Arrays.asList("start", "stop");

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!commandSender.hasPermission("TheManHunt.GameManagement.CmdGame")) return null;
        if (args.length == 1) {
            return arguments;
        }
        return null;
    }
}

package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.GameHandler;
import me.CubeLegend.TheManHunt.GameState;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.LanguageSystem.Message;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandTeam implements TabExecutor {

    private LanguageManager lManager = LanguageManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 1) return false;
        switch (args[0]) {
            case "join" -> {
                if (args.length != 2) return false;
                if (!(commandSender instanceof Player player)) {
                    //commandSender.sendMessage("§cYou need to be a player to join a team!");
                    lManager.sendMessage(commandSender, Message.ERROR_ONLY_FOR_PLAYERS, new String[0]);
                    return true;
                }
                if (!TeamHandler.getInstance().getTeams().contains(args[1])) {
                    //player.sendMessage("§4" + args[1] + " §cis not a valid team!");
                    lManager.sendMessage(player, Message.ERROR_INVALID_TEAM, new String[] {args[1]});
                    return true;
                }
                if (!GameHandler.getInstance().getGameState().equals(GameState.IDLE)) {
                    //player.sendMessage("§cThe game is already running");
                    lManager.sendMessage(player, Message.ERROR_GAME_IS_RUNNING, new String[0]);
                    return true;
                }
                TeamHandler.getInstance().getTeam(args[1]).addMember(player);
                //player.sendMessage("§6You joined team " + args[1] + "!");
                lManager.sendMessage(player, Message.YOU_JOINED_TEAM, new String[] {args[1]});
                return true;
            }
            case "list" -> {
                if (args.length != 2) return false;
                if (!TeamHandler.getInstance().getTeams().contains(args[1])) {
                    //commandSender.sendMessage("§4" + args[1] + " §cis not a valid team!");
                    lManager.sendMessage(commandSender, Message.ERROR_INVALID_TEAM, new String[] {args[1]});
                    return true;
                }
                //commandSender.sendMessage("§6Members of team " + args[1] + ":");
                lManager.sendMessage(commandSender, Message.MEMBERS_OF_TEAM_LISTED, new String[] {args[1]});
                List<Player> members = TeamHandler.getInstance().getTeam(args[1]).getMembers();
                for (int i = 0; i < 20; i++) {
                    if (members.size() == i) break;
                    commandSender.sendMessage(" §6- " + members.get(i).getName());
                }
                return true;
            }
            case "add" -> {
                if (args.length != 3) return false;
                Player player1 = Bukkit.getPlayer(args[1]);
                if (player1 == null) {
                    //commandSender.sendMessage("§4" + args[1] + " §cis not online!");
                    lManager.sendMessage(commandSender, Message.ERROR_PLAYER_IS_NOT_ONLINE, new String[] {args[1]});
                    return true;
                }
                if (!TeamHandler.getInstance().getTeams().contains(args[2])) {
                    //player1.sendMessage("§4" + args[2] + " §cis not a valid team!");
                    lManager.sendMessage(commandSender, Message.ERROR_INVALID_TEAM, new String[] {args[2]});
                    return true;
                }
                if (!GameHandler.getInstance().getGameState().equals(GameState.IDLE)) {
                    //commandSender.sendMessage("§cThe game is already running");
                    lManager.sendMessage(commandSender, Message.ERROR_GAME_IS_RUNNING, new String[0]);
                    return true;
                }
                TeamHandler.getInstance().getTeam(args[2]).addMember(player1);
                //commandSender.sendMessage("§6" + args[1] + " got added to team " + args[2]);
                //player1.sendMessage("§6You got added to team " + args[2]);
                lManager.sendMessage(commandSender, Message.OTHER_PLAYER_ADDED_TO_TEAM, new String[] {args[1], args[2]});
                lManager.sendMessage(player1, Message.YOU_JOINED_TEAM, new String[] {args[2]});
                return true;
            }
            case "remove" -> {
                if (args.length != 3) return false;
                Player player2 = Bukkit.getPlayer(args[1]);
                if (player2 == null) {
                    //commandSender.sendMessage("§4" + args[1] + " §cis not online!");
                    lManager.sendMessage(commandSender, Message.ERROR_PLAYER_IS_NOT_ONLINE, new String[] {args[1]});
                    return true;
                }
                if (!TeamHandler.getInstance().getTeams().contains(args[2])) {
                    //player2.sendMessage("§4" + args[2] + " §cis not a valid team!");
                    lManager.sendMessage(commandSender, Message.ERROR_INVALID_TEAM, new String[] {args[2]});
                    return true;
                }
                if (!GameHandler.getInstance().getGameState().equals(GameState.IDLE)) {
                    //commandSender.sendMessage("§cThe game is already running");
                    lManager.sendMessage(commandSender, Message.ERROR_GAME_IS_RUNNING, new String[0]);
                    return true;
                }
                TeamHandler.getInstance().getTeam(args[2]).removeMember(player2);
                //commandSender.sendMessage("§6" + args[1] + " got removed from team " + args[2]);
                //player2.sendMessage("§6You got removed from team " + args[2]);
                lManager.sendMessage(commandSender, Message.OTHER_PLAYER_REMOVED_FROM_TEAM, new String[] {args[1], args[2]});
                lManager.sendMessage(player2, Message.YOU_LEFT_TEAM, new String[] {args[2]});
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> operations = new ArrayList<>();
            operations.add("join");
            operations.add("list");
            operations.add("add");
            operations.add("remove");
            return operations;
        }
        if (args.length == 2) {
            if (args[0].equals("add") || args[0].equals("remove")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            } else {
                return TeamHandler.getInstance().getTeams();
            }
        }
        if (args.length == 3) {
            if (args[0].equals("add") || args[0].equals("remove")) {
                return TeamHandler.getInstance().getTeams();
            }
        }
        return null;
    }
}

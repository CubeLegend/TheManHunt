package me.CubeLegend.TheManHunt.LanguageSystem;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Language {

    public String name;

    private final File languageMessageFile;
    private FileConfiguration languageMessage;

    Language(File file) {
        languageMessageFile = file;
        createCustomConfig();
        if (!languageMessage.getBoolean("LanguageFile")) {
            LanguageManager.getInstance().removeLanguage(this);
        }
        name = languageMessage.getString("Name");
    }

    private void createCustomConfig() {
        if (!languageMessageFile.exists()) {
            languageMessageFile.getParentFile().mkdirs();
        }

        languageMessage = new YamlConfiguration();
        try {
            languageMessage.load(languageMessageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Player player, Message message, String[] args) {
        switch (message) {
            case ERROR_ONLY_FOR_PLAYERS -> {
                String text = languageMessage.getString("ERROR_ONLY_FOR_PLAYERS");
                if (text == null) return;
                player.sendMessage(text);
            }
            case ERROR_INVALID_TEAM -> {
                String text = languageMessage.getString("ERROR_INVALID_TEAM");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                player.sendMessage(text);
            }
            case ERROR_GAME_IS_RUNNING -> {
                String text = languageMessage.getString("ERROR_GAME_IS_RUNNING");
                if (text == null) return;
                player.sendMessage(text);
            }
            case ERROR_PLAYER_IS_NOT_ONLINE -> {
                String text = languageMessage.getString("ERROR_PLAYER_IS_NOT_ONLINE");
                if (text == null) return;
                if (text.contains("<player>")) text = text.replace("<player>", args[0]);
                player.sendMessage(text);
            }
            case YOU_JOINED_TEAM -> {
                String text = languageMessage.getString("YOU_JOINED_TEAM");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                player.sendMessage(text);
            }
            case YOU_LEFT_TEAM -> {
                String text = languageMessage.getString("YOU_LEFT_TEAM");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                player.sendMessage(text);
            }
            case OTHER_PLAYER_ADDED_TO_TEAM -> {
                String text = languageMessage.getString("OTHER_PLAYER_ADDED_TO_TEAM");
                if (text == null) return;
                if (text.contains("<player>")) text = text.replace("<player>", args[0]);
                if (text.contains("<team>")) text = text.replace("<team>", args[1]);
                player.sendMessage(text);
            }
            case OTHER_PLAYER_REMOVED_FROM_TEAM -> {
                String text = languageMessage.getString("OTHER_PLAYER_REMOVED_FROM_TEAM");
                if (text == null) return;
                if (text.contains("<player>")) text = text.replace("<player>", args[0]);
                if (text.contains("<team>")) text = text.replace("<team>", args[1]);
                player.sendMessage(text);
            }
            case MEMBERS_OF_TEAM_LISTED -> {
                String text = languageMessage.getString("MEMBERS_OF_TEAM_LISTED");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                player.sendMessage(text);
                for (int i = 1; i < args.length; i++) {
                    player.sendMessage(" §6- " + args[i]);
                }
            }
        }
    }

    public void sendMessageToConsole(Message message, String[] args) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        switch (message) {
            case ERROR_ONLY_FOR_PLAYERS -> {
                String text = languageMessage.getString("ERROR_ONLY_FOR_PLAYERS");
                if (text == null) return;
                console.sendMessage(text);
            }
            case ERROR_INVALID_TEAM -> {
                String text = languageMessage.getString("ERROR_INVALID_TEAM");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                console.sendMessage(text);
            }
            case ERROR_GAME_IS_RUNNING -> {
                String text = languageMessage.getString("ERROR_GAME_IS_RUNNING");
                if (text == null) return;
                console.sendMessage(text);
            }
            case ERROR_PLAYER_IS_NOT_ONLINE -> {
                String text = languageMessage.getString("ERROR_PLAYER_IS_NOT_ONLINE");
                if (text == null) return;
                if (text.contains("<player>")) text = text.replace("<player>", args[0]);
                console.sendMessage(text);
            }
            case YOU_JOINED_TEAM -> {
                String text = languageMessage.getString("YOU_JOINED_TEAM");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                console.sendMessage(text);
            }
            case OTHER_PLAYER_ADDED_TO_TEAM -> {
                String text = languageMessage.getString("OTHER_PLAYER_ADDED_TO_TEAM");
                if (text == null) return;
                if (text.contains("<player>")) text = text.replace("<player>", args[0]);
                if (text.contains("<team>")) text = text.replace("<team>", args[1]);
                console.sendMessage(text);
            }
            case MEMBERS_OF_TEAM_LISTED -> {
                String text = languageMessage.getString("MEMBERS_OF_TEAM_LISTED");
                if (text == null) return;
                if (text.contains("<team>")) text = text.replace("<team>", args[0]);
                console.sendMessage(text);
                for (int i = 1; i < args.length; i++) {
                    console.sendMessage(" §6- " + args[i]);
                }
            }
        }
    }
}

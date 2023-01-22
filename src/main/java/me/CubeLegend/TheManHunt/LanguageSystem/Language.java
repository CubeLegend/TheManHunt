package me.CubeLegend.TheManHunt.LanguageSystem;

import me.CubeLegend.TheManHunt.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Language {

    private final LanguageManager manager;
    private final File languageMessageFile;
    public String name;
    private FileConfiguration languageMessage;

    private final Configuration config = Configuration.getInstance();

    private String defaultColor = "§r";
    private String highlightColor = "§r";
    private String errorColor = "§c";
    private String errorHighlightColor = "§c";

    Language(LanguageManager languageManager, File file) {
        manager = languageManager;
        languageMessageFile = file;
        createCustomConfig();
        if (!languageMessage.getBoolean("LanguageFile")) {
            manager.removeLanguage(this);
        }
        name = languageMessage.getString("Name").toLowerCase();
        updateColors();
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

    private void updateColors() {
        StringBuilder sb = new StringBuilder();
        sb.append("§r");
        for (String s : config.getStringList("Message.DefaultColor")) {
            sb.append(ChatColor.valueOf(s));
        }
        defaultColor = sb.toString();

        sb = new StringBuilder();
        sb.append("§r");
        for (String s : config.getStringList("Message.HighlightedColor")) {
            sb.append(ChatColor.valueOf(s));
        }
        highlightColor = sb.toString();

        sb = new StringBuilder();
        sb.append("§r");
        for (String s : config.getStringList("Message.ErrorColor")) {
            sb.append(ChatColor.valueOf(s));
        }
        errorColor = sb.toString();

        sb = new StringBuilder();
        sb.append("§r");
        for (String s : config.getStringList("Message.ErrorHighlightedColor")) {
            sb.append(ChatColor.valueOf(s));
        }
        errorHighlightColor = sb.toString();
    }

    public String getMessage(Message message, String[] args) {
        switch (message) {
            case ERROR_ONLY_FOR_PLAYERS -> {
                String text = languageMessage.getString("ERROR_ONLY_FOR_PLAYERS");
                if (text == null) return null;
                return errorColor + text;
            }
            case ERROR_INVALID_TEAM -> {
                String text = languageMessage.getString("ERROR_INVALID_TEAM");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", errorHighlightColor + args[0] + errorColor);
                return errorColor + text;
            }
            case ERROR_GAME_IS_RUNNING -> {
                String text = languageMessage.getString("ERROR_GAME_IS_RUNNING");
                if (text == null) return null;
                return errorColor + text;
            }
            case ERROR_GAME_IS_NOT_RUNNING -> {
                String text = languageMessage.getString("ERROR_GAME_IS_NOT_RUNNING");
                if (text == null) return null;
                return errorColor + text;
            }
            case ERROR_PLAYER_IS_NOT_ONLINE -> {
                String text = languageMessage.getString("ERROR_PLAYER_IS_NOT_ONLINE");
                if (text == null) return null;
                if (text.contains("<player>")) text = text.replace("<player>", errorHighlightColor + args[0] + errorColor);
                return errorColor + text;
            }
            case ERROR_NOT_ENOUGH_TEAM_MEMBERS -> {
                String text = languageMessage.getString("ERROR_NOT_ENOUGH_TEAM_MEMBERS");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", errorHighlightColor + args[0] + errorColor);
                return text;
            }
            case ERROR_USE_ONLY_IN_OVERWORLD -> {
                String text = languageMessage.getString("ERROR_USE_ONLY_IN_OVERWORLD");
                return errorColor + text;
            }
            case ERROR_USE_ONLY_IN_NETHER -> {
                String text = languageMessage.getString("ERROR_USE_ONLY_IN_NETHER");
                return errorColor + text;
            }
            case YOU_JOINED_TEAM -> {
                String text = languageMessage.getString("YOU_JOINED_TEAM");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case YOU_LEFT_TEAM -> {
                String text = languageMessage.getString("YOU_LEFT_TEAM");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case OTHER_PLAYER_ADDED_TO_TEAM -> {
                String text = languageMessage.getString("OTHER_PLAYER_ADDED_TO_TEAM");
                if (text == null) return null;
                if (text.contains("<player>")) text = text.replace("<player>", highlightColor + args[0] + defaultColor);
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[1] + defaultColor);
                return defaultColor + text;
            }
            case OTHER_PLAYER_REMOVED_FROM_TEAM -> {
                String text = languageMessage.getString("OTHER_PLAYER_REMOVED_FROM_TEAM");
                if (text == null) return null;
                if (text.contains("<player>")) text = text.replace("<player>", highlightColor + args[0] + defaultColor);
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[1] + defaultColor);
                return defaultColor + text;
            }
            case MEMBERS_OF_TEAM_LISTED -> {
                String text = languageMessage.getString("MEMBERS_OF_TEAM_LISTED");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case GAME_STOPPED -> {
                String text = languageMessage.getString("GAME_STOPPED");
                if (text == null) return null;
                return defaultColor + text;
            }
            case HUNTER_NEAR -> {
                String text = languageMessage.getString("HUNTER_NEAR");
                if (text == null) return null;
                return defaultColor + text;
            }
            case COMPASS_POINTS_TO -> {
                String text = languageMessage.getString("COMPASS_POINTS_TO");
                if (text == null) return null;
                if (text.contains("<player>")) text = text.replace("<player>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case NEXT_VILLAGE_X_BLOCKS_AWAY -> {
                String text = languageMessage.getString("NEXT_VILLAGE_X_BLOCKS_AWAY");
                if (text == null) return null;
                if (text.contains("<distance>")) text = text.replace("<distance>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case NEXT_Fortress_X_BLOCKS_AWAY -> {
                String text = languageMessage.getString("NEXT_Fortress_X_BLOCKS_AWAY");
                if (text == null) return null;
                if (text.contains("<distance>")) text = text.replace("<distance>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case BROADCAST_WINNER -> {
                String text = languageMessage.getString("BROADCAST_WINNER");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case TEAM_HAS_WON -> {
                String text = languageMessage.getString("TEAM_HAS_WON");
                if (text == null) return null;
                return defaultColor + text;
            }
            case TEAM_HAS_WON_SUBTITLE -> {
                String text = languageMessage.getString("TEAM_HAS_WON_SUBTITLE");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case TEAM_HAS_LOST -> {
                String text = languageMessage.getString("TEAM_HAS_LOST");
                if (text == null) return null;
                return defaultColor + text;
            }
            case TEAM_HAS_LOST_SUBTITLE -> {
                String text = languageMessage.getString("TEAM_HAS_LOST_SUBTITLE");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case HUNTERS_RELEASED -> {
                String text = languageMessage.getString("HUNTERS_RELEASED");
                if (text == null) return null;
                return defaultColor + text;
            }
            case TIME_UNTIL_HUNTERS_RELEASED -> {
                String text = languageMessage.getString("TIME_UNTIL_HUNTERS_RELEASED");
                if (text == null) return null;
                if (text.contains("<time>")) text = text.replace("<time>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case RUNNERS_DISPLAY_NAME -> {
                String text = languageMessage.getString("RUNNERS_DISPLAY_NAME");
                if (text == null) return null;
                return defaultColor + text;
            }
            case HUNTERS_DISPLAY_NAME -> {
                String text = languageMessage.getString("HUNTERS_DISPLAY_NAME");
                if (text == null) return null;
                return defaultColor + text;
            }
            case SPECTATORS_DISPLAY_NAME -> {
                String text = languageMessage.getString("SPECTATORS_DISPLAY_NAME");
                if (text == null) return null;
                return defaultColor + text;
            }
            case JOIN_TEAM_BUTTON -> {
                String text = languageMessage.getString("JOIN_TEAM_BUTTON");
                if (text == null) return null;
                if (text.contains("<team>")) text = text.replace("<team>", highlightColor + args[0] + defaultColor);
                return defaultColor + text;
            }
            case TEAM_SELECTION_TITLE -> {
                String text = languageMessage.getString("TEAM_SELECTION_TITLE");
                if (text == null) return null;
                return defaultColor + text;
            }
        }
        return null;
    }
}

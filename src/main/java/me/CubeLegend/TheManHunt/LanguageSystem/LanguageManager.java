package me.CubeLegend.TheManHunt.LanguageSystem;

import me.CubeLegend.TheManHunt.Settings;
import me.CubeLegend.TheManHunt.TheManHunt;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LanguageManager implements Listener {

    private static LanguageManager instance;

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    private final HashMap<UUID, String> playerLanguages = new HashMap<>();

    private final HashMap<String, Language> languages = new HashMap<>();

    private String defaultLanguage;

    File languagesDir = new File(TheManHunt.getInstance().getDataFolder(), "languages");

    LanguageManager() {
        languagesDir.mkdirs();
        File[] files = languagesDir.listFiles();

        assert files != null;
        if (files.length == 0) {
            createLanguageFromSource("english.yml");
            return;
        }
        for (File file : files) {
            Language language = new Language(file);
            languages.put(language.name, language);
        }
    }

    private void createLanguageFromSource(String filename) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = new File(languagesDir, filename);
        try {
            inputStream = TheManHunt.getInstance().getResource(filename);
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            Language language = new Language(file);
            languages.put(language.name, language);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeLanguage(Language l) {
        languages.remove(l);
    }

    public List<Language> getLanguages() {
        return languages.values().stream().toList();
    }

    public String getPlayerLanguage(Player player) {
        return playerLanguages.get(player.getUniqueId());
    }

    public void setDefaultLanguage(String language) {
        if (!languages.containsKey(language)) {
            Bukkit.getConsoleSender().sendMessage("§4Default language doesn't exist");
            return;
        }
        defaultLanguage = language;
    }

    public void setPlayerLanguage(Player player, String language) {
        if (languages.containsKey(language)) {
            playerLanguages.put(player.getUniqueId(), language);
        }
    }

    public void sendMessage(CommandSender commandSender, Message message, String[] args) {
        if (commandSender instanceof Player player) {
            sendMessage(player, message, args);
        } else if (commandSender instanceof ConsoleCommandSender) {
            sendMessageToConsole(message, args);
        }
    }

    public void sendMessage(Player player, Message message, String[] args) {
        String playerLanguageString = playerLanguages.get(player.getUniqueId());
        Language playerLanguage = languages.get(playerLanguageString);
        String text = playerLanguage.getMessage(message, args);
        if (text == null) return;
        player.sendMessage(text);
    }

    public void sendMessageToConsole(Message message, String[] args) {
        Language language = languages.get(defaultLanguage);
        String text = language.getMessage(message, args);
        if (text == null) return;
        Bukkit.getConsoleSender().sendMessage(text);
    }

    public void broadcastMessage(Message message, String[] args) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerLanguageString = playerLanguages.get(player.getUniqueId());
            Language playerLanguage = languages.get(playerLanguageString);
            String text = playerLanguage.getMessage(message, args);
            if (text == null) return;
            player.sendMessage(text);
        }
    }

    public void sendActionbar(Player player, Message message, String[] args) {
        String playerLanguageString = playerLanguages.get(player.getUniqueId());
        Language playerLanguage = languages.get(playerLanguageString);
        String text = playerLanguage.getMessage(message, args);
        if (text == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public void sendTitle(Player player, Message titleMessage, Message subtitleMessage, String[] args) {
        String playerLanguageString = playerLanguages.get(player.getUniqueId());
        Language playerLanguage = languages.get(playerLanguageString);
        String title = playerLanguage.getMessage(titleMessage, args);
        String subtitle = playerLanguage.getMessage(subtitleMessage, args);
        if (title == null) return;
        if (subtitle == null) return;
        Settings settings = Settings.getInstance();
        player.sendTitle(title, subtitle, settings.TitleFadeIn, settings.TitleStay, settings.TitleFadeOut);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerLanguages.containsKey(player.getUniqueId())) {
            setPlayerLanguage(player, defaultLanguage);
        }
    }
}

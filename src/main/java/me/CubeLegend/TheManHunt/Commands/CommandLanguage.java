package me.CubeLegend.TheManHunt.Commands;

import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandLanguage implements TabExecutor {

    List<String> languages = new ArrayList<>();

    public CommandLanguage() {
        LanguageManager.getInstance().getLanguages().forEach(language -> languages.add(language.name));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!commandSender.hasPermission("TheManHunt.Players.CmdLanguage")) return true;
        if (args.length != 1) return false;
        String language = args[0].toLowerCase();
        if (languages.contains(language) && commandSender instanceof Player player) {
            LanguageManager.getInstance().setPlayerLanguage(player, language);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}

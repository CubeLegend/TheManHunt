package me.CubeLegend.TheManHunt.Bstats;

import me.CubeLegend.TheManHunt.PersistentDataHandler;
import me.CubeLegend.TheManHunt.Settings;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BstatsHandler {

    JavaPlugin plugin;
    int pluginId;

    public BstatsHandler(JavaPlugin plugin, int pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(plugin, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie("default_language", () -> Settings.getInstance().DefaultLanguage));
        metrics.addCustomChart(new Metrics.AdvancedPie("team_wins", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("Runner wins", PersistentDataHandler.getInstance().allRunnerWins);
            valueMap.put("Hunter wins", PersistentDataHandler.getInstance().allHunterWins);
            return valueMap;
        }));
    }
}

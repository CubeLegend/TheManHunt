package me.CubeLegend.TheManHunt.Bstats;

import me.CubeLegend.TheManHunt.Settings;
import org.bukkit.plugin.java.JavaPlugin;

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
    }
}

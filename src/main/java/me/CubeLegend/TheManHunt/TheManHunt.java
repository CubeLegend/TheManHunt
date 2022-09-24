package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.Bstats.BstatsHandler;
import me.CubeLegend.TheManHunt.Commands.CommandGame;
import me.CubeLegend.TheManHunt.Commands.CommandLanguage;
import me.CubeLegend.TheManHunt.Commands.CommandTeam;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.SpecialAbilities.HunterNearWarning;
import me.CubeLegend.TheManHunt.SpecialAbilities.OneHitKill;
import me.CubeLegend.TheManHunt.StateSystem.GameHandler;
import me.CubeLegend.TheManHunt.StateSystem.GameState;
import me.CubeLegend.TheManHunt.TeamSystem.SelectionInventories;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import me.CubeLegend.TheManHunt.TeamSystem.TeamSelectionItem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TheManHunt extends JavaPlugin {

    private static TheManHunt instance;

    public static TheManHunt getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        Settings.getInstance().loadSettingsFromConfig();

        if (Settings.getInstance().DeleteWorldOnStartUp) {
            if (!PersistentDataHandler.getInstance().deleteWorldOnStartUp.equals("")) {
                String worldName = PersistentDataHandler.getInstance().deleteWorldOnStartUp;
                List<Path> worlds = new ArrayList<>();
                Path wc = Bukkit.getWorldContainer().toPath();
                worlds.add(Path.of(wc.toString(), worldName));
                worlds.add(Path.of(wc.toString(), worldName + "_nether"));
                worlds.add(Path.of(wc.toString(), worldName + "_the_end"));
                Bukkit.getConsoleSender().sendMessage("The world: " + worldName + " and all of its dimensions get deleted");
                Path datapacksDir = Path.of(Bukkit.getWorldContainer().toPath().toString(), worldName, "datapacks");
                for (Path world : worlds) {
                    try (Stream<Path> worldPaths = Files.walk(world)) {
                        worldPaths
                                .sorted(Comparator.reverseOrder())
                                .filter(Predicate.not(path -> path.startsWith(datapacksDir)))
                                .filter(Files::isRegularFile)
                                .forEach(path -> {
                                    try {
                                        Files.delete(path);
                                    } catch (IOException e) {
                                        Bukkit.getLogger().warning("Couldn't delete file " + path + " Cause: " + e.getCause());
                                    }
                                });
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Couldn't walk world folder " + world + " Cause: " + e.getCause());
                    }
                }
                PersistentDataHandler.getInstance().deleteWorldOnStartUp = "";
                PersistentDataHandler.getInstance().saveData();
            }
        }
    }

    @Override
    public void onEnable() {
        //instance = this;

        this.saveDefaultConfig();
        //Settings.getInstance().loadSettingsFromConfig();
        new BstatsHandler(this, 16041).loadMetrics();

        new UpdateChecker(this, 105044).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("TheManHunt is up to date");
            } else {
                getLogger().info("There is a new update for TheManHunt available");
            }
        });

        TeamHandler.getInstance().registerScoreBoard();

        registerCommands();
        registerListeners();
        registerPluginMessagingChannels();
        //config.options().copyDefaults(true);
        //this.saveConfig();
        LanguageManager.getInstance().setDefaultLanguage(Settings.getInstance().DefaultLanguage);

        startRoutines();
        GameHandler.getInstance().setGameState(GameState.IDLE);
    }

    @Override
    public void onDisable() {
        unregisterPluginMessagingChannels();
        FreezeVision.getInstance().stopFreezeVisionRoutine();
        VillageTracker.getInstance().stopVillageTrackingRoutine();
        RunnerTracker.getInstance().stopRunnerTrackerRoutine();
        HunterNearWarning.getInstance().stopRoutine();
    }

    private void startRoutines() {
        if (Settings.getInstance().FreezeVision) {
            FreezeVision.getInstance().startFreezeVisionRoutine(Settings.getInstance().FreezeVisionUpdatePeriod);
        }
        if (Settings.getInstance().VillageTracker) {
            VillageTracker.getInstance().startVillageTrackingRoutine(Settings.getInstance().VillageTrackerUpdatePeriod);
        }
        if (Settings.getInstance().RunnerTracker) {
            RunnerTracker.getInstance().startRunnerTrackerRoutine(Settings.getInstance().RunnerTrackerUpdatePeriod);
        }
        if (Settings.getInstance().HunterNearWarning) {
            HunterNearWarning.getInstance().startRoutine(Settings.getInstance().HunterNearWarningUpdatePeriod, Settings.getInstance().HunterNearWarningRadius);
        }
    }

    private void registerCommands() {
        this.getCommand("game").setExecutor(new CommandGame());
        this.getCommand("team").setExecutor(new CommandTeam());
        this.getCommand("language").setExecutor(new CommandLanguage());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(TeamSelectionItem.getInstance(), this);
        pm.registerEvents(VillageTracker.getInstance(), this);
        pm.registerEvents(RunnerTracker.getInstance(), this);
        pm.registerEvents(Freeze.getInstance(), this);
        pm.registerEvents(new RunnerWin(), this);
        pm.registerEvents(new PlayerDeathHandler(), this);
        pm.registerEvents(TeamHandler.getInstance(), this);
        pm.registerEvents(OneHitKill.getInstance(), this);
        pm.registerEvents(LanguageManager.getInstance(), this);
        pm.registerEvents(SelectionInventories.getInstance(), this);
        pm.registerEvents(new LobbyHandler(), this);
        pm.registerEvents(HunterWaitTimer.getInstance(), this);
        pm.registerEvents(PersistentDataHandler.getInstance(), this);
        pm.registerEvents(new HunterCatchUp(), this);
    }

    private void registerPluginMessagingChannels() {
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")), new MessageListener());
    }

    private void unregisterPluginMessagingChannels() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, Objects.requireNonNull(this.getConfig().getString("PluginMessagingChannelOfMiniGame")));
    }

}

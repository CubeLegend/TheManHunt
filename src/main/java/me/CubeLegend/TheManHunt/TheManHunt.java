package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.Bstats.BstatsHandler;
import me.CubeLegend.TheManHunt.Commands.CommandGame;
import me.CubeLegend.TheManHunt.Commands.CommandLanguage;
import me.CubeLegend.TheManHunt.Commands.CommandTeam;
import me.CubeLegend.TheManHunt.Compass.FortressTracker;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.SpecialAbilities.*;
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

    private Configuration config;

    @Override
    public void onLoad() {
        instance = this;

        if (this.getConfig().getBoolean("DeleteWorldOnStartUp", false)) {
            if (!PersistentDataHandler.getInstance().deleteWorldOnStartUp.equals("")) {
                String worldName = PersistentDataHandler.getInstance().deleteWorldOnStartUp;
                deleteWorld(worldName, this.getConfig().getBoolean("DeleteDatapacksOnStartUp", false));
                PersistentDataHandler.getInstance().deleteWorldOnStartUp = "";
                PersistentDataHandler.getInstance().saveData();
            }
        }
    }

    private void deleteWorld(String worldName, boolean deleteDatapacks) {
        List<Path> worlds = new ArrayList<>();
        Path wc = Bukkit.getWorldContainer().toPath();
        worlds.add(Path.of(wc.toString(), worldName));
        worlds.add(Path.of(wc.toString(), worldName + "_nether"));
        worlds.add(Path.of(wc.toString(), worldName + "_the_end"));
        Bukkit.getLogger().info("The world: " + worldName + " and all of its dimensions get deleted");
        Path datapacksDir = Path.of(wc.toString(), worldName, "datapacks");
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
        if (deleteDatapacks) {
            try (Stream<Path> datapackPaths = Files.walk(datapacksDir)) {
                datapackPaths
                        .skip(1)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                Bukkit.getLogger().warning("Couldn't delete file " + path + " Cause: " + e.getCause());
                            }
                        });
            } catch (IOException e) {
                Bukkit.getLogger().warning("Couldn't walk datapacks folder " + datapacksDir + " Cause: " + e.getCause());
            }
        }
    }

    @Override
    public void onEnable() {
        //instance = this;

        config = Configuration.getInstance();

        //Settings.getInstance().loadSettingsFromConfig();
        new BstatsHandler(this, 16041).loadMetrics();

        checkForNewUpdate(105044);

        TeamHandler.getInstance().registerScoreBoard();

        registerCommands();
        registerListeners();
        //config.options().copyDefaults(true);
        //this.saveConfig();
        LanguageManager.getInstance().setDefaultLanguage(config.getString("Default.Language"));

        startRoutines();
    }

    @Override
    public void onDisable() {
        FreezeVision.getInstance().stopFreezeVisionRoutine();
        FortressTracker.getInstance().stopFortressTrackingRoutine();
        VillageTracker.getInstance().stopVillageTrackingRoutine();
        RunnerTracker.getInstance().stopRunnerTrackerRoutine();
        HunterNearWarning.getInstance().stopRoutine();
    }

    private void startRoutines() {
        if (config.getBoolean("Abilities.Runner.FreezeVision")) {
            FreezeVision.getInstance().startFreezeVisionRoutine(config.getInt("UpdatePeriod.FreezeVision"));
        }
        if (config.getBoolean("Abilities.Runner.FortressTracker")) {
            FortressTracker.getInstance().startFortressTrackingRoutine(config.getInt("UpdatePeriod.FortressTracker"));
        }
        if (config.getBoolean("Abilities.Runner.VillageTracker")) {
            System.out.println("VillageTracker: True");
            VillageTracker.getInstance().startVillageTrackingRoutine(config.getInt("UpdatePeriod.VillageTracker"));
        }
        if (config.getBoolean("Abilities.Hunter.RunnerTracker")) {
            RunnerTracker.getInstance().startRunnerTrackerRoutine(config.getInt("UpdatePeriod.RunnerTracker"));
        }
        if (config.getInt("Abilities.Runner.HunterNearWarning") != 0) {
            HunterNearWarning.getInstance().startRoutine(config.getInt("UpdatePeriod.HunterNearWarning"), config.getInt("Abilities.Runner.HunterNearWarning"));
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
        pm.registerEvents(FortressTracker.getInstance(), this);
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
        pm.registerEvents(new RespawnNearRunner(), this);
    }

    private void checkForNewUpdate(int resourceId) {
        new UpdateChecker(this, resourceId).getVersion(version -> {
            int[] onlineVersion = getVersionAsIntArray(version);
            int[] thisVersion = getVersionAsIntArray(this.getDescription().getVersion());

            for (int i = 0; i < onlineVersion.length; i++) {
                if (i >= thisVersion.length) {
                    if (0 < onlineVersion[i]) {
                        logUpdateInfo(version, this.getDescription().getVersion());
                        break;
                    }
                    continue;
                }
                if (onlineVersion[i] > thisVersion[i]) {
                    logUpdateInfo(version, this.getDescription().getVersion());
                    break;
                }
            }
        });
    }

    private void logUpdateInfo(String onlineVersion, String thisVersion) {
        getLogger().info("There is a new update for TheManHunt available");
        getLogger().info("Available version: " + onlineVersion);
        getLogger().info("Installed version: " + thisVersion);
    }

    private int[] getVersionAsIntArray(String version) {
        String[] versionStr = version.split("\\.");
        for (int i = 0; i < versionStr.length; i++) {
            versionStr[i] = versionStr[i].replaceAll("[^0-9]","");
        }
        return Arrays.stream(versionStr).mapToInt(Integer::parseInt).toArray();
    }
}

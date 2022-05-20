package me.CubeLegend.TheManHunt;

import me.CubeLegend.TheManHunt.Commands.CommandGame;
import me.CubeLegend.TheManHunt.Commands.CommandTeam;
import me.CubeLegend.TheManHunt.Compass.RunnerTracker;
import me.CubeLegend.TheManHunt.Compass.VillageTracker;
import me.CubeLegend.TheManHunt.LanguageSystem.LanguageManager;
import me.CubeLegend.TheManHunt.SpecialAbilities.FreezeVision;
import me.CubeLegend.TheManHunt.SpecialAbilities.HunterNearWarning;
import me.CubeLegend.TheManHunt.SpecialAbilities.OneHitKill;
import me.CubeLegend.TheManHunt.TeamSystem.Team;
import me.CubeLegend.TheManHunt.TeamSystem.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
            if (!DataConfig.getInstance().getWorldToDelete().equals("")) {
                String worldName = DataConfig.getInstance().getWorldToDelete();
                List<File> worlds = new ArrayList<>();
                worlds.add(new File(Bukkit.getWorldContainer(), worldName));
                worlds.add(new File(Bukkit.getWorldContainer(), worldName + "_nether"));
                worlds.add(new File(Bukkit.getWorldContainer(), worldName + "_the_end"));
                Bukkit.getConsoleSender().sendMessage("The world: " + worldName + " and all of its dimensions gets deleted");
                for (File world : worlds) {
                    try {
                        Files.walk(world.toPath())
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    world.mkdirs();
                }
                (new File(worlds.get(0), "advancements")).mkdirs();
                (new File(worlds.get(0), "datapacks")).mkdirs();
                (new File(worlds.get(0), "playerdata")).mkdirs();
                (new File(worlds.get(0), "stats")).mkdirs();
                for (File world : worlds) {
                    (new File(world, "data")).mkdirs();
                    (new File(world, "entities")).mkdirs();
                    (new File(world, "poi")).mkdirs();
                    (new File(world, "region")).mkdirs();
                }
                DataConfig.getInstance().setWorldToDelete("");
            }
        }
    }

    @Override
    public void onEnable() {
        //instance = this;

        this.saveDefaultConfig();
        //Settings.getInstance().loadSettingsFromConfig();
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
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(VillageTracker.getInstance(), this);
        pm.registerEvents(RunnerTracker.getInstance(), this);
        pm.registerEvents(Freeze.getInstance(), this);
        pm.registerEvents(new RunnerWin(), this);
        pm.registerEvents(new PlayerDeathHandler(), this);
        pm.registerEvents(TeamHandler.getInstance(), this);
        pm.registerEvents(OneHitKill.getInstance(), this);
        pm.registerEvents(LanguageManager.getInstance(), this);
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

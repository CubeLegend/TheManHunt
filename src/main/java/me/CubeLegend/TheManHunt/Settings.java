package me.CubeLegend.TheManHunt;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings {

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public Settings() {

    }

    private final FileConfiguration config = TheManHunt.getInstance().getConfig();

    public String PluginMessagingChannel = "themanhunt:minigame";
    public boolean DeleteWorldOnStartUp = false;

    public List<String> DefaultMessageColor = List.of("GOLD");
    public List<String> HighlightedMessageColor = List.of("GOLD", "UNDERLINE", "BOLD");
    public List<String> ErrorMessageColor = List.of("RED");
    public List<String> ErrorHighlightedMessageColor = List.of("RED", "UNDERLINE", "BOLD");

    public String DefaultLanguage = "English";

    public boolean FreezeVision = false;
    public boolean OneHitKill = false;
    public boolean HunterNearWarning = false;
    public boolean VillageTracker = false;
    public boolean RunnerTracker = false;

    public int HunterWaitTimer = 10;
    public int HunterNearWarningRadius = 50;

    public int FreezeVisionUpdatePeriod = 1;
    public int HunterNearWarningUpdatePeriod = 1;
    public int VillageTrackerUpdatePeriod = 1;
    public int RunnerTrackerUpdatePeriod = 1;
    public int CompassSpinningUpdatePeriod = 1;

    public boolean AlwaysUpdateRunnerTracker = true;
    public boolean AlwaysSetLodestone = false;

    public boolean VillageTrackerUseLodestone = false;

    public void loadSettingsFromConfig() {
        PluginMessagingChannel = config.getString("PluginMessagingChannelOfMiniGame");
        DeleteWorldOnStartUp = config.getBoolean("DeleteWorldOnStartUp");
        if (config.isList("DefaultMessageColor"))
            DefaultMessageColor = config.getStringList("DefaultMessageColor");
        if (config.isList("HighlightedMessageColor"))
            HighlightedMessageColor = config.getStringList("HighlightedMessageColor");
        if (config.isList("ErrorMessageColor"))
            ErrorMessageColor = config.getStringList("ErrorMessageColor");
        if (config.isList("ErrorHighlightedMessageColor"))
            ErrorHighlightedMessageColor = config.getStringList("ErrorHighlightedMessageColor");
        DefaultLanguage = config.getString("DefaultLanguage");

        FreezeVision = config.getBoolean("FreezeVision");
        OneHitKill = config.getBoolean("OneHitKill");
        HunterNearWarning = config.getBoolean("HunterNearWarning");
        VillageTracker = config.getBoolean("VillageTracker");
        RunnerTracker = config.getBoolean("RunnerTracker");

        HunterWaitTimer = config.getInt("HunterWaitTimer");
        HunterNearWarningRadius = config.getInt("HunterNearWarningRadius");

        FreezeVisionUpdatePeriod = config.getInt("FreezeVisionUpdatePeriod");
        HunterNearWarningUpdatePeriod = config.getInt("HunterNearWarningUpdatePeriod");
        VillageTrackerUpdatePeriod = config.getInt("VillageTrackerUpdatePeriod");
        RunnerTrackerUpdatePeriod = config.getInt("RunnerTrackerUpdatePeriod");
        CompassSpinningUpdatePeriod = config.getInt("CompassSpinningUpdatePeriod");

        AlwaysUpdateRunnerTracker = config.getBoolean("AlwaysUpdateRunnerTracker");
        AlwaysSetLodestone = config.getBoolean("AlwaysSetLodestone");

        VillageTrackerUseLodestone = config.getBoolean("VillageTrackerUseLodestone");
    }

    public void safeSettingsToConfig() {
        config.set("PluginMessagingChannelOfMiniGame", PluginMessagingChannel);
        config.set("DeleteWorldOnStartUp", DeleteWorldOnStartUp);

        config.set("FreezeVision", FreezeVision);
        config.set("HunterNearWarning", HunterNearWarning);
        config.set("VillageTracker", VillageTracker);
        config.set("RunnerTracker", RunnerTracker);

        config.set("HunterWaitTimer", HunterWaitTimer);
        config.set("HunterNearWarningRadius", HunterNearWarningRadius);

        config.set("FreezeVisionUpdatePeriod", FreezeVisionUpdatePeriod);
        config.set("HunterNearWarningUpdatePeriod", HunterNearWarningUpdatePeriod);
        config.set("VillageTrackerUpdatePeriod", VillageTrackerUpdatePeriod);
        config.set("RunnerTrackerUpdatePeriod", RunnerTrackerUpdatePeriod);
        config.set("CompassSpinningUpdatePeriod", CompassSpinningUpdatePeriod);

        config.set("AlwaysUpdateRunnerTracker", AlwaysUpdateRunnerTracker);
        config.set("AlwaysSetLodestone", AlwaysSetLodestone);

        config.set("VillageTrackerUseLodestone", VillageTrackerUseLodestone);
        TheManHunt.getInstance().saveConfig();
    }
}

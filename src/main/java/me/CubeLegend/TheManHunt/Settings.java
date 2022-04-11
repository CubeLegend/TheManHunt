package me.CubeLegend.TheManHunt;

import org.bukkit.configuration.file.FileConfiguration;

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

    public boolean FreezeVision = false;
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

    public boolean SetLocationOfPlayerCompassIfPossible = true;

    public void loadSettingsFromConfig() {
        PluginMessagingChannel = config.getString("PluginMessagingChannelOfMiniGame");
        DeleteWorldOnStartUp = config.getBoolean("DeleteWorldOnStartUp");

        FreezeVision = config.getBoolean("FreezeVision");
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
        TheManHunt.getInstance().saveConfig();
    }
}

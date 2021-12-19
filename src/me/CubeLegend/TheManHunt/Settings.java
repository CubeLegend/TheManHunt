package me.CubeLegend.TheManHunt;

public class Settings {

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public String PluginMessagingChannel = "themanhunt:minigame";

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
        PluginMessagingChannel = TheManHunt.getInstance().getConfig().getString("PluginMessagingChannelOfMiniGame");

        //TODO setup these settings in the config file and load them
    }
}

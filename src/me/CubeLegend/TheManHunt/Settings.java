package me.CubeLegend.TheManHunt;

public class Settings {

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public boolean freezeVision = false;
}

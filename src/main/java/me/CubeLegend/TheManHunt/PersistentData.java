package me.CubeLegend.TheManHunt;

import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PersistentData implements Serializable {

    @Serial
    private static final long serialVersionUID = 3570970814172486251L;

    public String deleteWorldOnStartUp;

    public List<UUID> runners;
    public List<UUID> hunters;

    public int allRunnerWins;
    public int allHunterWins;

    // Can be used for saving
    public PersistentData(
            String deleteWorldOnStartUp,
            List<UUID> runners,
            List<UUID> hunters,
            int allRunnerWins,
            int allHunterWins
    ) {
        this.deleteWorldOnStartUp = deleteWorldOnStartUp;

        this.runners = runners;
        this.hunters = hunters;

        this.allRunnerWins = allRunnerWins;
        this.allHunterWins = allHunterWins;
    }
    // Can be used for loading
    public PersistentData(PersistentData loadedPersistentData) {
        this.deleteWorldOnStartUp = loadedPersistentData.deleteWorldOnStartUp;

        this.runners = loadedPersistentData.runners;
        this.hunters = loadedPersistentData.hunters;

        this.allRunnerWins = loadedPersistentData.allRunnerWins;
        this.allHunterWins = loadedPersistentData.allHunterWins;
    }

    public boolean saveData(String filePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }
    public static PersistentData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            PersistentData persistentData = (PersistentData) in.readObject();
            in.close();
            return persistentData;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
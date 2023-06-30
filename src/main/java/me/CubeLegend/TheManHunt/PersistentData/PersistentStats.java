package me.CubeLegend.TheManHunt.PersistentData;

import org.bukkit.Bukkit;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PersistentStats implements Serializable {

    @Serial
    private static final long serialVersionUID = -839975967040811882L;

    public int allRunnerWins;
    public int allHunterWins;

    // Can be used for saving
    public PersistentStats(
            int allRunnerWins,
            int allHunterWins
    ) {
        this.allRunnerWins = allRunnerWins;
        this.allHunterWins = allHunterWins;
    }

    public void saveData(String filePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save data to file");
            e.printStackTrace();
        }
    }

    public static PersistentStats loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            PersistentStats persistentStats = (PersistentStats) in.readObject();
            in.close();
            return persistentStats;
        } catch (ClassNotFoundException | IOException e) {
            Bukkit.getLogger().warning("Could not load data to file");
            e.printStackTrace();
            return null;
        }
    }
}